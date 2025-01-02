package page.admin.common.utils.email;

import java.util.Random;

public class CodeGenerator {
    public static String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10)); // 0~9 숫자 생성
        }
        return code.toString();
    }
}
