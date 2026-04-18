package com.voting.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class VoteSubmitRequest {
    @NotEmpty(message = "候选人ID列表不能为空")
    @Size(min = 5, max = 10, message = "投票数量必须在5-10之间")
    private List<Long> candidateIds;
}
