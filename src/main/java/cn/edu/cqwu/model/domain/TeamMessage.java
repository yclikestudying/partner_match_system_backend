package cn.edu.cqwu.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName team_message
 */
@TableName(value ="team_message")
@Data
public class TeamMessage implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 发送用户id
     */
    private Long fromUserId;

    /**
     * 队伍id

     */
    private Long teamId;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 发送时间
     */
    private Date createTime;

    /**
     * 0 - 未删除
1 - 已删除
     */
    private Integer idDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}