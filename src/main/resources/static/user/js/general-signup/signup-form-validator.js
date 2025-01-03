// 회원가입 폼 관련 유효성 검사 로직

/**
 * 아이디 유효성 검사
 * - 영문자와 숫자만 허용
 * - 최소 4자 이상
 */
export const validateUserId = (userId) => {
    const validUserIdRegex = /^[a-zA-Z0-9]*$/;
    if (!validUserIdRegex.test(userId)) {
        return { valid: false, message: "아이디는 영문자와 숫자만 입력 가능합니다." };
    }
    if (userId.length < 4) {
        return { valid: false, message: "아이디는 4자 이상이어야 합니다." };
    }
    return { valid: true, message: "유효한 아이디 형식입니다." };
};

/**
 * 휴대전화 번호 유효성 검사
 * - 형식: 010-xxxx-xxxx
 */
export const validateMobilePhone = (mobilePhone) => {
    const validMobilePhoneRegex = /^010-\d{4}-\d{4}$/;
    if (!validMobilePhoneRegex.test(mobilePhone)) {
        return { valid: false, message: "유효한 휴대전화 번호를 입력하세요. (예: 010-1234-5678)" };
    }
    return { valid: true, message: "유효한 휴대전화 번호입니다." };
};

/**
 * 이메일 유효성 검사
 * - 기본 이메일 형식 준수
 */
export const validateEmail = (email) => {
    const validEmailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!validEmailRegex.test(email)) {
        return { valid: false, message: "유효한 이메일 형식을 입력하세요. (예: example@domain.com)" };
    }
    return { valid: true, message: "유효한 이메일 형식입니다." };
};

/**
 * 비밀번호 유효성 검사
 * - 4자 이상 16자 이하
 */
export const validatePassword = (password) => {
    const validPasswordRegex = /^.{4,16}$/; // 모든 문자 포함, 4자 이상 16자 이하
    if (!validPasswordRegex.test(password)) {
        return {
            valid: false,
            message: "비밀번호는 4~16자로 입력해주세요.",
        };
    }
    return { valid: true, message: "유효한 비밀번호 형식입니다." };
};


