package hanghae99.clonecoding.airbnb.security;

import hanghae99.clonecoding.airbnb.entity.Member;
import hanghae99.clonecoding.airbnb.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Collections;

@RequiredArgsConstructor
@Component
public class OAuthLoginProvider implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final MemberRepository repository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User user = delegate.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        OAuthAttributes attr = OAuthAttributes.of(registrationId, userNameAttributeName, user.getAttributes());
        saveOrUpdate(attr);
        return new DefaultOAuth2User(Collections.emptyList(), attr.getAttributes(), userNameAttributeName);
    }

    // DB 유무 확인
    private void saveOrUpdate(OAuthAttributes attributes) {
        Member member = repository.findByEmail(attributes.getEmail());
        if (member == null)
            member = Member.builder().email(attributes.getEmail()).build();
        member.updateMember(attributes.getName(), attributes.getPicture());
        repository.save(member);
        attributes.setMember(member);
    }
}