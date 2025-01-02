package page.admin.user.member.domain;

public enum SecurityQuestion {
    FAVORITE_COLOR("가장 좋아하는 색은?"),
    FIRST_PET("첫 반려동물의 이름은?"),
    BEST_FRIEND("가장 친한 친구의 이름은?"),
    BIRTH_CITY("태어난 도시 이름은?");

    private final String question;

    SecurityQuestion(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }
}