package cn.edu.cqwu.model.dto.userTeam;

import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author 杨闯
 */
@Data
public class UserJoinTeamDto {
    /**
     * 队伍成员id
     */
    @NotNull
    private Long userId;

    /**
     * 队伍id
     */
    @NotNull
    private Long teamId;
}
