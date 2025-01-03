import { validateUserId, validateMobilePhone, validateEmail, validatePassword } from './general-signup/signup-form-validator.js';
import { checkDuplicateId } from './general-signup/signup-form-api.js';
import { initPasswordValidation } from './general-signup/signup-form-password.js';
import { initPhoneFormatter } from './general-signup/signup-form-phone-formatter.js';

/**
 * 유효성 검사 결과 표시 공통 함수
 * @param {HTMLElement} element - 메시지를 표시할 DOM 요소
 * @param {boolean} isValid - 유효성 검사 결과
 * @param {string} message - 표시할 메시지
 */
const showValidationMessage = (element, isValid, message) => {
    element.textContent = message;
    element.style.color = isValid ? "green" : "red";
};

/**
 * 유효성 검사 초기화 함수
 * @param {string} inputId - 입력 필드 ID
 * @param {function} validator - 유효성 검사 함수
 * @param {string} messageId - 메시지를 표시할 요소 ID
 */
const initializeValidation = (inputId, validator, messageId) => {
    const inputElement = document.getElementById(inputId);
    const messageElement = document.getElementById(messageId);

    inputElement.addEventListener('input', function () {
        const { valid, message } = validator(inputElement.value);
        showValidationMessage(messageElement, valid, message);
    });
};

// 비밀번호 유효성 검사
initializeValidation("password", validatePassword, "passwordMessage");

// 비밀번호 확인 로직 초기화
initPasswordValidation('password', 'passwordConfirm');

// 아이디 유효성 검사 및 중복 확인
document.getElementById('userId').addEventListener('input', async function () {
    const userId = this.value;
    const userIdMessage = document.getElementById('userIdMessage');

    const validation = validateUserId(userId);
    if (!validation.valid) {
        showValidationMessage(userIdMessage, false, validation.message);
        return;
    }

    try {
        const isDuplicate = await checkDuplicateId(userId);
        const message = isDuplicate
            ? "이미 사용 중인 아이디입니다."
            : "사용 가능한 아이디입니다.";
        showValidationMessage(userIdMessage, !isDuplicate, message);
    } catch (error) {
        showValidationMessage(userIdMessage, false, "중복 검사 중 오류가 발생했습니다.");
    }
});

// 휴대전화 번호 포맷팅 및 유효성 검사
initPhoneFormatter('mobilePhone');
initializeValidation('mobilePhone', validateMobilePhone, 'mobilePhoneMessage');

// 이메일 유효성 검사
initializeValidation('email', validateEmail, 'emailMessage');
