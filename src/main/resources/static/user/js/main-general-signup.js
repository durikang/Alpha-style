// main-general-signup.js

import {
    validateUserIdWithDuplicate,
    validateEmail,
    validateMobilePhone,
    validatePassword,
    validateGender
} from './general-signup/signup-form-validator.js';
import { initPasswordValidation } from './general-signup/signup-form-password.js';
import { initPhoneFormatter } from './general-signup/signup-form-phone-formatter.js';
import { Navigation } from './general-signup/navigation.js';
import { EmailAuth } from './general-signup/email-auth.js';
import { AddressSearch } from './general-signup/address-search.js';
import { UIManager } from './general-signup/uiManager.js'; // UIManager import

document.addEventListener('DOMContentLoaded', () => {
    console.log('DOMContentLoaded 이벤트 발생.');

    // (1) 단계 전환
    const steps = document.querySelectorAll('.step');
    console.log(`찾은 단계 수: ${steps.length}`);
    Navigation.init(steps, '.to-step');

    // (2) 이메일 인증
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
        toStep2Button: document.querySelector('.to-step[data-target-step="1"]'), // "다음 단계" 버튼
    });

    // (3) 비밀번호 확인 로직
    initPasswordValidation('password', 'passwordConfirm');

    // --------------------------------------------------
    // 아이디: 'input' 이벤트 시 즉시 검사
    // --------------------------------------------------
    const userIdInput = document.getElementById('userId');
    const userIdMessage = document.getElementById('userIdMessage');

    if (userIdInput && userIdMessage) {
        userIdInput.addEventListener('input', async () => {
            const userId = userIdInput.value.trim();
            if (!userId) {
                // 값이 비어있으면 메시지 없애기
                userIdMessage.textContent = '';
                await UIManager.hideElementAsync(userIdMessage);
                return;
            }
            // 유효성 + 중복 검사
            const { valid, duplicate } = await validateUserIdWithDuplicate(userId, userIdMessage);
            // 메시지 색
            if (!valid || duplicate) {
                userIdMessage.style.color = 'red';
            } else {
                userIdMessage.style.color = 'green';
            }
        });
    }

    // --------------------------------------------------
    // 이메일: 'input' 이벤트 시 즉시 검사
    // --------------------------------------------------
    const emailInputElement = document.getElementById('email');
    const emailMessageElement = document.getElementById('emailMessage');

    if (emailInputElement && emailMessageElement) {
        emailInputElement.addEventListener('input', async () => {
            const emailValue = emailInputElement.value.trim();
            console.log('Email input event triggered with value:', emailValue);
            if (!emailValue) {
                emailMessageElement.textContent = '';
                await UIManager.hideElementAsync(emailMessageElement); // 숨기기
                return;
            }
            const { valid } = await validateEmail(emailValue, emailMessageElement); // 메시지 색상은 validateEmail 함수 내에서 이미 설정됨
        });
    }

    // --------------------------------------------------
    // 비밀번호: 'input' 이벤트 시 즉시 검사
    // --------------------------------------------------
    const passwordInput = document.getElementById('password');
    const passwordMessage = document.getElementById('passwordMessage');

    if (passwordInput && passwordMessage) {
        passwordInput.addEventListener('input', () => {
            const value = passwordInput.value.trim();
            if (!value) {
                passwordMessage.textContent = '';
                UIManager.hideElementAsync(passwordMessage);
                return;
            }
            const { valid } = validatePassword(value, passwordMessage);
            passwordMessage.style.color = valid ? 'green' : 'red';
        });
    }

    // --------------------------------------------------
    // 휴대전화
    // --------------------------------------------------
    initPhoneFormatter('mobilePhone');
    const mobilePhoneInput = document.getElementById('mobilePhone');
    const mobilePhoneMessage = document.getElementById('mobilePhoneMessage');

    if (mobilePhoneInput && mobilePhoneMessage) {
        mobilePhoneInput.addEventListener('input', async () => {
            const phoneValue = mobilePhoneInput.value.trim();
            if (!phoneValue) {
                mobilePhoneMessage.textContent = '';
                await UIManager.hideElementAsync(mobilePhoneMessage);
                return;
            }
            const { valid } = await validateMobilePhone(phoneValue, mobilePhoneMessage);
            mobilePhoneMessage.style.color = valid ? 'green' : 'red';
        });
    }

    // --------------------------------------------------
    // 성별: 'change' 이벤트
    // --------------------------------------------------
    const genderSelect = document.getElementById('gender');
    const genderMessage = document.getElementById('genderMessage');

    if (genderSelect && genderMessage) {
        genderSelect.addEventListener('change', async () => { // async 추가
            const genderValue = genderSelect.value.trim();
            if (!genderValue) {
                genderMessage.textContent = '';
                await UIManager.hideElementAsync(genderMessage); // 숨기기
                return;
            }
            const { valid } = await validateGender(genderValue, genderMessage);
            genderMessage.style.color = valid ? 'green' : 'red';
            await UIManager.showElementAsync(genderMessage); // 보이기
        });
    }

    // --------------------------------------------------
    // 주소 찾기
    // --------------------------------------------------
    AddressSearch.initialize({
        searchAddressBtn: document.getElementById('searchAddressBtn'),
        zipCodeInput: document.getElementById('zipCode'),
        basicAddressInput: document.getElementById('basicAddress'),
        secondaryAddressInput: document.getElementById('secondaryAddress'),
    });
});
