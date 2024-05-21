package cn.edu.cqwu.model.dto.teamMessage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 杨闯
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserGetTeamMessageDto {
    /**
     * 队伍id
     */
    private Long teamId;

    /**
     * 队伍成员id
     */
    private List<Long> ids;
}
