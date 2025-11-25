package com.kfh.clinic.config.security;

import java.io.Serializable;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String sessionId;
	private String username;
	private Instant sessionExpiresAt;
}

