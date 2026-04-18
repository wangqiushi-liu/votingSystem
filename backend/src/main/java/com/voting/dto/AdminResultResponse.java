package com.voting.dto;

import lombok.Data;

@Data
public class AdminResultResponse {
    private Long candidateId;
    private String name;
    private String department;
    private String avatar;
    private Integer voteCount;
    private Integer rank;
}
