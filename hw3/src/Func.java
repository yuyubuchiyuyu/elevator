public class Func {
    public static String[] findFunc(String str, char type) {
        int index = str.indexOf(type);
        String[] expression = new String[4];
        for (int i = 0; i < 4; i++) {
            expression[i] = "";
        }
        index = index + 2;//+1是"("
        int left = 1;
        int right = 0;
        int sign = 1;
        int writeFlag = 1;
        if (str.charAt(index) == '(') {
            left++;
        }
        while (left != right) {
            expression[0] = expression[0] + str.charAt(index);
            if (writeFlag == 1) {
                expression[sign] = expression[sign] + str.charAt(index);
            }
            index++;
            if (str.charAt(index) == ',' && left == right + 1) {
                sign++;
                writeFlag = 0;//","不写入
            } else {
                if (str.charAt(index) == '(') {
                    left++;
                } else if (str.charAt(index) == ')') {
                    right++;
                }
                writeFlag = 1;
            }
        }
        return expression;
    }

    public static String replace(String str, String funcF, String funcG, String funcH) {
        String newStr = str;
        while (newStr.contains("f") || newStr.contains("g") || newStr.contains("h")) {
            char type;
            String func;
            if (newStr.contains("f")) {
                type = 'f';
                func = funcF.replace("exp", "EXP").
                        replace("x", "a").replace("y", "b").replace("z", "c")
                        .replace("EXP", "exp");
            } else if (newStr.contains("g")) {
                type = 'g';
                func = funcG.replace("exp", "EXP").
                        replace("x", "a").replace("y", "b").replace("z", "c")
                        .replace("EXP", "exp");
            } else {
                type = 'h';
                func = funcH.replace("exp", "EXP").
                        replace("x", "a").replace("y", "b").replace("z", "c")
                        .replace("EXP", "exp");
            }
            int equalsIndex = func.indexOf('=');
            String funcName = func.substring(2, equalsIndex - 1); //得到f()括号中的东西
            String funcExpr = func.substring(equalsIndex + 1); //得到f函数中等号之后的内容
            String[] funcArgument = funcName.split(","); //得到f()中的x，y，z
            String[] expression = findFunc(newStr, type);//得到express中对应参量
            String temp = funcExpr;
            for (int i = 0; i < funcArgument.length; i++) {
                temp = temp.replace(funcArgument[i],
                        "(" + expression[i + 1] + ")"); //将f中的变量换成str中的参数
            }
            newStr = newStr.replace(type + "(" + expression[0] + ")", "(" + temp + ")");
        }
        return newStr;
    }

    public static String funcDefine(String str, String funcF, String funcG, String funcH) {
        String head = "";
        String body = "";
        int sign = 0;
        head = head + str.charAt(sign); // str[0]='f'/'g'/'h'
        sign++; // str[1]='('
        int left = 1;
        int right = 0;
        while (left != right) {
            head = head + str.charAt(sign);
            sign++;
            if (str.charAt(sign) == '(') {
                left++;
            } else if (str.charAt(sign) == ')') {
                right++;
            }
        }
        head = head + str.charAt(sign); // 此时sign指向')'
        sign++; // sign指向'='
        sign++;
        while (sign < str.length()) {
            body = body + str.charAt(sign);
            sign++;
        }
        body = replace(body, funcF, funcG, funcH);
        return head + '=' + body;
    }
}
