package com.example.demo.dto.authaccountDTO.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacebookUserResponse {

    private String id;

    private String name;

    private String email;

    private String gender;
}
