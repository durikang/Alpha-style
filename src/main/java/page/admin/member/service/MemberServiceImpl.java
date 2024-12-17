package page.admin.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import page.admin.member.domain.Member;
import page.admin.member.domain.dto.UpdateForm;
import page.admin.member.repository.MemberRepository;

import java.util.List;
import java.util.Optional;

@Service
@Primary
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public Optional<Member> findByUserIdAndPassword(String userId, String password) {
        return memberRepository.findByUserIdAndPassword(userId, password);
    }

    @Override
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    @Override
    public Member getMemberById(Long userNo) {
        return memberRepository.findById(userNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. ID: " + userNo));
    }

    @Override
    public Member saveMember(Member member) {
        return memberRepository.save(member);
    }

    @Override
    public void deleteMember(Long userNo) {
        memberRepository.deleteById(userNo);
    }

    @Override
    public Optional<Member> findByUserId(String userId) {
        return memberRepository.findByUserId(userId);
    }

    @Override
    public UpdateForm getUpdateFormById(Long userNo) {
        Member member = getMemberById(userNo);

        // 기존 Member 데이터를 UpdateForm으로 변환
        UpdateForm updateForm = new UpdateForm();
        updateForm.setUserNo(member.getUserNo());
        updateForm.setUserId(member.getUserId());
        updateForm.setUsername(member.getUsername());
        updateForm.setEmail(member.getEmail());
        updateForm.setMobilePhone(member.getMobilePhone());
        updateForm.setAddress(member.getAddress());
        updateForm.setSecondaryAddress(member.getSecondaryAddress());
        updateForm.setZipCode(member.getZipCode());
        updateForm.setRole(member.getRole());

        return updateForm;
    }

    @Override
    public void editMemberInfo(Long userNo, UpdateForm updateForm) {
        Member existingMember = getMemberById(userNo);

        // UpdateForm의 데이터로 필드 업데이트
        existingMember.setUsername(updateForm.getUsername());
        existingMember.setEmail(updateForm.getEmail());
        existingMember.setMobilePhone(updateForm.getMobilePhone());
        existingMember.setAddress(updateForm.getAddress());
        existingMember.setSecondaryAddress(updateForm.getSecondaryAddress());
        existingMember.setZipCode(updateForm.getZipCode());
        existingMember.setSecondaryAddress(updateForm.getSecondaryAddress());
        existingMember.setRole(updateForm.getRole());

        // 비밀번호가 입력된 경우에만 업데이트
        if (updateForm.getPassword() != null && !updateForm.getPassword().isEmpty()) {
            existingMember.setPassword(updateForm.getPassword());
        }

        // 변경된 정보 저장
        memberRepository.save(existingMember);
    }

}
