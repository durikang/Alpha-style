export const checkDuplicateId = async (userId) => {
    try {
        const response = await fetch(`/user/signup/check-duplicate-id?userId=${encodeURIComponent(userId)}`);
        return await response.json();
    } catch (error) {
        console.error("AJAX 요청 실패:", error);
        throw new Error("중복 확인 중 오류가 발생했습니다.");
    }
};
