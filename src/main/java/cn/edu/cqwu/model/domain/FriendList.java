package cn.edu.cqwu.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 好友列表
 * @TableName friend_list
 */
@TableName(value ="friend_list")
@Data
public class FriendList implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 请求方
     */
    private Long fromUserId;

    /**
     * 同意方
     */
    private Long toUserId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 0 - 等待请求
     * 1 - 请求完成
     */
    private Integer status;

    /**
     * 0 - 未删除
     * 1 - 删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}