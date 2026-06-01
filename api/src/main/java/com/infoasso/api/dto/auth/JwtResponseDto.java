package com.infoasso.api.dto.auth;

import com.infoasso.api.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponseDto {

  private String token;
  private final String type = "Bearer";
  private Long id;
  private String email;
  private String role;
}
