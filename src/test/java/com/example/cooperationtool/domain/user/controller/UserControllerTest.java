package com.example.cooperationtool.domain.user.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.cooperationtool.domain.user.dto.request.SignupRequestDto;
import com.example.cooperationtool.domain.user.dto.response.SignupResponseDto;
import com.example.cooperationtool.domain.user.entity.UserRoleEnum;
import com.example.cooperationtool.domain.user.service.UserService;
import com.example.cooperationtool.domain.user.support.MockSpringSecurityFilter;
import com.example.cooperationtool.global.config.WebSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(
    controllers = {UserController.class},
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = WebSecurityConfig.class
        )
    }
)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    UserService userService;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity(new MockSpringSecurityFilter()))
            .alwaysDo(print())
            .build();
    }

    @Nested
    @DisplayName("유저 회원가입")
    class SignUp {

        @DisplayName("성공 케이스")
        @Test
        void success() throws Exception {
            SignupRequestDto requestDto = SignupRequestDto.builder()
                .username("username")
                .password("12341234")
                .nickname("nickname")
                .introduce("introduce")
                .build();

            String json = objectMapper.writeValueAsString(requestDto);

            SignupResponseDto responseDto = SignupResponseDto.builder()
                .username(requestDto.getUsername())
                .nickname(requestDto.getNickname())
                .role(UserRoleEnum.USER)
                .build();

            given(userService.signUp(requestDto)).willReturn(responseDto);

            mockMvc.perform(post("/api/user/signup")
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(status().isOk(),
                    jsonPath("$.code").value("200"),
                    jsonPath("$.message").value("회원가입 성공"));
        }
    }
}