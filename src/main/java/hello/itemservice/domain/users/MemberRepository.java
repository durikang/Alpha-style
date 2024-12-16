package hello.itemservice.domain.users;

import hello.itemservice.domain.users.Member;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MemberRepository {

    private static final Map<Long, Member> store = new HashMap<>(); // static storage
    private static final AtomicLong sequence = new AtomicLong(0L); // static sequence generator

    // 회원 저장
    public Member save(Member member) {
        member.setUserNo(sequence.incrementAndGet()); // ID 자동 생성
        store.put(member.getUserNo(), member);
        return member;
    }

    // 회원 조회 (ID로)
    public Optional<Member> findById(Long userNo) {
        return Optional.ofNullable(store.get(userNo));
    }

    // 모든 회원 조회
    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }

    // 회원 업데이트
    public void update(Long userNo, Member updatedMember) {
        Member member = store.get(userNo);
        if (member != null) {
            member.setUserId(updatedMember.getUserId());
            member.setUsername(updatedMember.getUsername());
            member.setPassword(updatedMember.getPassword());
            member.setEmail(updatedMember.getEmail());
            member.setMobilePhone(updatedMember.getMobilePhone());
            member.setAddress(updatedMember.getAddress());
            member.setZipCode(updatedMember.getZipCode());
            member.setSecondaryAddress(updatedMember.getSecondaryAddress());
            member.setBirthDate(updatedMember.getBirthDate());
            member.setSecurityQuestion(updatedMember.getSecurityQuestion());
            member.setSecurityAnswer(updatedMember.getSecurityAnswer());
            member.setRole(updatedMember.getRole());
        }
    }

    // 회원 삭제
    public void delete(Long userNo) {
        store.remove(userNo);
    }

    // 모든 데이터 삭제 (테스트용)
    public void clearStore() {
        store.clear();
    }
}
