// signup-form-validator.js
import { checkDuplicateId } from './signup-form-api.js';

/**
 * 아이디 유효성 + 중복 검사 (단순 메시지 표시)
 */
export const validateUserIdWithDuplicate = async (userId, messageElement) => {
    let newMessage = '';
    let valid = false;
    let duplicate = false;

    // 1) 기본 유효성
    const validUserIdRegex = /^[a-zA-Z0-9]*$/;
    if (!validUserIdRegex.test(userId)) {
        newMessage = "아이디는 영문자와 숫자만 입력 가능합니다.";
    } else if (userId.length < 4) {
        newMessage = "아이디는 4자 이상이어야 합니다.";
    } else {
        // 2) 중복 검사
        try {
            const data = await checkDuplicateId(userId); // { duplicate: true/false } 가정
            if (data.duplicate) {
                newMessage = "이미 사용 중인 아이디입니다.";
                valid = false;
                duplicate = true;
            } else {
                newMessage = "사용 가능한 아이디입니다.";
                valid = true;
            }
        } catch (error) {
            newMessage = error.message || "중복 확인 중 오류가 발생했습니다.";
            valid = false;
        }
    }

    // 메시지 표시 및 색상 설정
    if (newMessage) {
        messageElement.textContent = newMessage;
        messageElement.style.display = 'block'; // 즉시 표시
        messageElement.style.color = newMessage.includes("사용 가능한 아이디입니다.") ? 'green' : 'red';
    } else {
        messageElement.textContent = '';
        messageElement.style.display = 'none'; // 즉시 숨김
    }

    return { valid, duplicate };
};

/**
 * 이메일 유효성 검사 (단순 메시지 표시)
 */
export const validateEmail = async (email, messageElement) => {
    let newMessage = '';
    const validEmailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!validEmailRegex.test(email)) {
        newMessage = "유효한 이메일 형식이 아닙니다.";
    } else {
        newMessage = "유효한 이메일 형식입니다.";
    }

    // 메시지 표시 및 색상 설정
    if (newMessage) {
        messageElement.textContent = newMessage;
        messageElement.style.display = 'block'; // 즉시 표시
        messageElement.style.color = newMessage.includes("유효한 이메일 형식입니다.") ? 'green' : 'red';
    } else {
        messageElement.textContent = '';
        messageElement.style.display = 'none'; // 즉시 숨김
    }

    const valid = newMessage.includes("유효한 이메일 형식입니다.");
    return { valid };
};

/**
 * 비밀번호 유효성 검사 (단순 메시지 표시)
 */
export const validatePassword = async (password, messageElement) => {
    let newMessage = '';
    const validPasswordRegex = /^.{4,16}$/;

    if (!validPasswordRegex.test(password)) {
        newMessage = "비밀번호는 4~16자로 입력해주세요.";
    } else {
        newMessage = "유효한 비밀번호 형식입니다.";
    }

    // 메시지 표시 및 색상 설정
    if (newMessage) {
        messageElement.textContent = newMessage;
        messageElement.style.display = 'block'; // 즉시 표시
        messageElement.style.color = (newMessage === "유효한 비밀번호 형식입니다.") ? 'green' : 'red';
    } else {
        messageElement.textContent = '';
        messageElement.style.display = 'none'; // 즉시 숨김
    }

    const valid = newMessage.includes("유효한 비밀번호 형식입니다.");
    return { valid };
};

/**
 * 휴대전화 번호 유효성 검사 (단순 메시지 표시)
 */
export const validateMobilePhone = async (mobilePhone, messageElement) => {
    let newMessage = '';
    const validMobilePhoneRegex = /^010-\d{4}-\d{4}$/;

    if (!validMobilePhoneRegex.test(mobilePhone)) {
        newMessage = "휴대전화 번호는 010-xxxx-xxxx 형식이어야 합니다.";
    } else {
        newMessage = "유효한 휴대전화 번호입니다.";
    }

    // 메시지 표시 및 색상 설정
    if (newMessage) {
        messageElement.textContent = newMessage;
        messageElement.style.display = 'block'; // 즉시 표시
        messageElement.style.color = newMessage.includes("유효한 휴대전화 번호입니다.") ? 'green' : 'red';
    } else {
        messageElement.textContent = '';
        messageElement.style.display = 'none'; // 즉시 숨김
    }

    const valid = newMessage.includes("유효한 휴대전화 번호입니다.");
    return { valid };
};

/**
 * 성별 유효성 검사 (단순 메시지 표시)
 */
export const validateGender = async (gender, messageElement) => {
    let newMessage = '';

    if (!gender) {
        newMessage = "성별을 선택해 주세요.";
    } else if (gender !== "남" && gender !== "여" && gender !== "기타") {
        newMessage = "성별은 '남', '여', '기타' 중 하나를 선택해야 합니다.";
    } else {
        newMessage = "유효한 성별입니다.";
    }

    // 메시지 표시 및 색상 설정
    if (newMessage) {
        messageElement.textContent = newMessage;
        messageElement.style.display = 'block'; // 즉시 표시
        messageElement.style.color = newMessage.includes("유효한 성별입니다.") ? 'green' : 'red';
    } else {
        messageElement.textContent = '';
        messageElement.style.display = 'none'; // 즉시 숨김
    }

    const valid = newMessage.includes("유효한 성별입니다.");
    return { valid };
};
