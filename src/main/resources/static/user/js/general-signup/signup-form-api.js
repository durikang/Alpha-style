// signup-form-api.js

/**
 * 아이디 중복 검사 API 호출
 * @param {string} userId - 검사할 아이디
 * @returns {Promise<{ duplicate: boolean }>} - 중복 여부 반환
 */
export const checkDuplicateId = async (userId) => {
    try {
        const response = await fetch(`/user/signup/check-duplicate-id?userId=${encodeURIComponent(userId)}`);
        if (!response.ok) {
            throw new Error('아이디 중복 검사에 실패했습니다.');
        }
        const data = await response.json();
        return { duplicate: data.duplicate };
    } catch (error) {
        console.error("AJAX 요청 실패:", error);
        throw new Error("중복 확인 중 오류가 발생했습니다.");
    }
};
