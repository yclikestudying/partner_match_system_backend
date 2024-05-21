package cn.edu.cqwu.model.vo.team;

import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author 杨闯
 */
@Data
@Builder
public class TeamInfoVO implements Serializable {
    private static final long serialVersionUID = 873492948940601339L;
    /**
     * 队伍id
     */
    @NotNull
    private Long id;

    /**
     * 队伍名称
     */
    @NotNull
    private String name;

    /**
     * 描述
     */
    @NotNull
    private String description;

    /**
     * 最大人数
     */
    @NotNull
    private Integer maxNum;

    /**
     * 队长id
     */
    @NotNull
    private Long userId;

    /**
     * 创建时间
     */
    @NotNull
    private Date createTime;

    /**
     *  加入成员id
     */
    @Nullable
    private List<Long> ids;

    /**
     * 头像
     */
    @Nullable
    private String avatarUrl;
}
