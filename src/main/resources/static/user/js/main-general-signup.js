import { Navigation } from './general-signup/navigation.js';
import { EmailAuth } from './general-signup/email-auth.js';

document.addEventListener('DOMContentLoaded', () => {

    const steps = document.querySelectorAll('.step');

    const buttons = {
        step1Next: document.getElementById('toStep2'),
        step2Prev: document.getElementById('toStep1'),
        step2Next: document.getElementById('toStep3'),
        step3Prev: document.getElementById('toStep2'),
        step3Next: document.getElementById('toStep4'),
        step4Prev: document.getElementById('toStep3'),
    };

    Navigation.init(steps, '.to-step');
    // 이메일 인증 초기화
    EmailAuth.init({
        sendCodeBtn: document.getElementById('sendCodeBtn'),
        emailInput: document.getElementById('email'),
        emailLabel: document.querySelector('label[for="email"]'),
        emailMessage: document.getElementById('emailMessage'),
        verifyCodeBtn: document.getElementById('verifyCodeBtn'),
        verificationCodeInput: document.getElementById('verificationCode'),
        verificationCodeContainer: document.getElementById('verificationCodeContainer'), // 추가
        codeLabel: document.querySelector('label[for="verificationCode"]'),
        codeMessage: document.getElementById('codeMessage'),
        timerMessage: document.getElementById('timerMessage'),
        authCompleteMessage: document.getElementById('authCompleteMessage'),
        toStep2Button: document.querySelector('.to-step[data-target-step="2"]'),
    });

});
