// 기존 유효성 검사 모듈 가져오기
import { validateUserId, validateMobilePhone, validateEmail, validatePassword } from './general-signup/signup-form-validator.js';
import { checkDuplicateId } from './general-signup/signup-form-api.js';
import { initPasswordValidation } from './general-signup/signup-form-password.js';
import { initPhoneFormatter } from './general-signup/signup-form-phone-formatter.js';

// 기존 단계 전환 및 이메일 인증 모듈 가져오기
import { Navigation } from './general-signup/navigation.js';
import { EmailAuth } from './general-signup/email-auth.js';

// 문서 로드 후 초기화
document.addEventListener('DOMContentLoaded', () => {
    const steps = document.querySelectorAll('.step');

    // 단계 전환 초기화
    Navigation.init(steps, '.to-step');

    // 이메일 인증 초기화
    EmailAuth.init({
        sendCodeBtn: document.getElementById('sendCodeBtn'),
        emailInput: document.getElementById('email'),
        emailLabel: document.querySelector('label[for="email"]'),
        emailMessage: document.getElementById('emailMessage'),
        verifyCodeBtn: document.getElementById('verifyCodeBtn'),
        verificationCodeInput: document.getElementById('verificationCode'),
        verificationCodeContainer: document.getElementById('verificationCodeContainer'),
        codeLabel: document.querySelector('label[for="verificationCode"]'),
        codeMessage: document.getElementById('codeMessage'),
        timerMessage: document.getElementById('timerMessage'),
        authCompleteMessage: document.getElementById('authCompleteMessage'),
        toStep2Button: document.querySelector('.to-step[data-target-step="2"]'),
    });

    // 유효성 검사 초기화
    initializeValidation("password", validatePassword, "passwordMessage");
    initPasswordValidation('password', 'passwordConfirm');

    // 아이디 유효성 검사 및 중복 확인
    const userIdInput = document.getElementById('userId');
    const userIdMessage = document.getElementById('userIdMessage');
    if (userIdInput && userIdMessage) {
        userIdInput.addEventListener('input', async function () {
            const userId = this.value;

            const validation = validateUserId(userId);
            if (!validation.valid) {
                userIdMessage.textContent = validation.message;
                userIdMessage.style.color = 'red';
                return;
            }

            try {
                const isDuplicate = await checkDuplicateId(userId);
                userIdMessage.textContent = isDuplicate
                    ? "이미 사용 중인 아이디입니다."
                    : "사용 가능한 아이디입니다.";
                userIdMessage.style.color = isDuplicate ? 'red' : 'green';
            } catch (error) {
                userIdMessage.textContent = "중복 검사 중 오류가 발생했습니다.";
                userIdMessage.style.color = 'red';
            }
        });
    }

    // 휴대전화 번호 포맷팅 및 유효성 검사 초기화
    initPhoneFormatter('mobilePhone');
    initializeValidation('mobilePhone', validateMobilePhone, 'mobilePhoneMessage');

    // 이메일 유효성 검사 초기화
    initializeValidation('email', validateEmail, 'emailMessage');
});

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

    if (inputElement && messageElement) {
        inputElement.addEventListener('input', function () {
            const { valid, message } = validator(inputElement.value);
            showValidationMessage(messageElement, valid, message);
        });
    }
};
