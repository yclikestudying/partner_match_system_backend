package cn.edu.cqwu.controller;

import cn.edu.cqwu.common.BaseResponse;
import cn.edu.cqwu.common.CodeMessage;
import cn.edu.cqwu.common.ResultUtils;
import cn.edu.cqwu.exception.BusinessException;
import cn.edu.cqwu.model.domain.FriendList;
import cn.edu.cqwu.model.domain.Message;
import cn.edu.cqwu.model.domain.User;
import cn.edu.cqwu.model.dto.message.GetUserMessageDto;
import cn.edu.cqwu.model.dto.message.SendMessageToUserDto;
import cn.edu.cqwu.model.dto.user.UserLoginDto;
import cn.edu.cqwu.model.dto.user.UserRegisterDto;
import cn.edu.cqwu.model.dto.user.UserSelectConditionsDto;
import cn.edu.cqwu.model.vo.friendList.FriendListVO;
import cn.edu.cqwu.model.vo.message.MessageVO;
import cn.edu.cqwu.model.vo.user.UserInfoVO;
import cn.edu.cqwu.service.FriendListService;
import cn.edu.cqwu.service.MessageService;
import cn.edu.cqwu.service.UserService;
import cn.edu.cqwu.websocket.AddFriendWS;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Result;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author 杨闯
 */
@RestController
@RequestMapping("/user")
@Slf4j
@Api(tags = "用户模块")
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private FriendListService friendListService;
    @Resource
    private AddFriendWS addFriendWS;
    @Resource
    private MessageService messageService;

    @PostMapping("/register")
    @ApiOperation("用户注册")
    public BaseResponse<CodeMessage> userRegister(@RequestBody UserRegisterDto userRegisterDto) {
        boolean result = userService.userRegister(userRegisterDto.getUserAccount(),
                userRegisterDto.getUserPassword(),
                userRegisterDto.getCheckPassword());

        if (!result) {
            return ResultUtils.error(CodeMessage.REGISTER_ERROR);
        }

        return ResultUtils.success(CodeMessage.REGISTER_SUCCESS);
    }

    @PostMapping("/login")
    @ApiOperation("用户登录")
    public BaseResponse<UserInfoVO> userLogin(@RequestBody UserLoginDto userLoginDto, HttpServletRequest request) {
        UserInfoVO userInfoVO = userService.userLogin(userLoginDto.getUserAccount(),
                userLoginDto.getUserPassword(), request);
        if (userInfoVO == null) {
            return ResultUtils.error(CodeMessage.LOGIN_ERROR);
        }

        return ResultUtils.success(CodeMessage.LOGIN_SUCCESS, userInfoVO);
    }

    @GetMapping("/getAvatarUrl")
    @ApiOperation("账号输入完获取该账号的头像")
    public BaseResponse getAvatarUrl(String userAccount) {
        return ResultUtils.success(userService.getAvatarUrl(userAccount));
    }

    @GetMapping("/selectAll/{current}/{id}")
    @ApiOperation("查询除当前用户以外的所有用户")
    public BaseResponse selectAllUser(@PathVariable Integer current, @PathVariable Integer id) {
        Page<User> pageList = userService.selectAllUser(current, id);

        return ResultUtils.success(CodeMessage.SELECT_SUCCESS, pageList);
    }

    @PostMapping("/selectByConditions/{current}")
    @ApiOperation("指定条件查询用户")
    public BaseResponse selectByConditions(@RequestBody UserSelectConditionsDto conditions, @PathVariable Integer current) {
        Page<User> pageList = userService.selectByConditions(conditions, current);

        return ResultUtils.success(CodeMessage.SELECT_SUCCESS, pageList);
    }

    @GetMapping("/selectById/{id}")
    @ApiOperation("根据id查询用户")
    public BaseResponse<UserInfoVO> selectById(@PathVariable Integer id) {
        UserInfoVO userInfoVO = userService.selectById(id);
        if (userInfoVO == null) {
            return ResultUtils.error(CodeMessage.SELECT_ERROR);
        }
        return ResultUtils.success(CodeMessage.SELECT_SUCCESS, userInfoVO);
    }

    @GetMapping("/deleteById/{id}")
    @ApiOperation("根据id删除用户")
    public BaseResponse<CodeMessage> deleteById(@PathVariable Integer id) {
        boolean result = userService.deleteById(id);
        if (!result) {
            return ResultUtils.error(CodeMessage.DELETE_ERROR);
        }

        return ResultUtils.success(CodeMessage.DELETE_SUCCESS);
    }

    @PostMapping("/updateById")
    @ApiOperation("根据id修改用户信息")
    public BaseResponse<CodeMessage> updateById(@RequestBody User user) {
        boolean result = userService.updateById(user);

        if (!result) {
            return ResultUtils.error(CodeMessage.UPDATE_ERROR);
        }

        return ResultUtils.success(CodeMessage.UPDATE_SUCCESS);
    }

    @PostMapping("/updateImg/{id}")
    @ApiOperation("用户修改头像")
    public BaseResponse<String> updateImg(@RequestBody MultipartFile multipartFile, @PathVariable Integer id) throws IOException {
        String path = userService.updateImg(multipartFile, id);
        if (StringUtils.isBlank(path)) {
            return ResultUtils.error(CodeMessage.UPDATE_ERROR);
        }
        return ResultUtils.success(CodeMessage.UPDATE_SUCCESS, path);
    }

    @PostMapping("/updateTags")
    @ApiOperation("用户修改标签")
    public BaseResponse<CodeMessage> updateTags(@RequestBody User user) {
        boolean result = userService.updateTags(user);
        if (result) {
            return ResultUtils.success(CodeMessage.UPDATE_SUCCESS);
        }

        return ResultUtils.error(CodeMessage.UPDATE_ERROR);
    }

    @PostMapping("/selectByTags/{id}")
    @ApiOperation("按标签搜索用户")
    public BaseResponse<List<UserInfoVO>> selectByTags(@RequestBody String tags, @PathVariable Long id) {
        List<UserInfoVO> userInfoVOS = userService.selectByTags(tags, id);
        if (userInfoVOS.size() != 0) {
            return ResultUtils.success(CodeMessage.SELECT_SUCCESS, userInfoVOS);
        }
        return ResultUtils.error(CodeMessage.SELECT_ERROR);
    }

    @PostMapping("/recommend/{id}")
    @ApiOperation("推荐相似用户")
    public BaseResponse recommend(@RequestBody String tags, @PathVariable Long id) {
        List<UserInfoVO> recommendUsers = userService.recommend(tags, id);
        if (recommendUsers == null) {
            return ResultUtils.error(CodeMessage.SELECT_ERROR);
        }
        return ResultUtils.success(CodeMessage.SELECT_SUCCESS, recommendUsers);
    }

    @GetMapping("/logout")
    @ApiOperation("退出登录")
    public void logout(Long currentUserId) {
           addFriendWS.removeKey(currentUserId);
    }

    @PostMapping("/addFriend")
    @ApiOperation("添加好友")
    public BaseResponse<CodeMessage> addFriend(@RequestBody FriendList friendList) {
        boolean result = friendListService.addFriend(friendList);
        if (result) {
            return ResultUtils.success(CodeMessage.JOIN_SUCCESS);
        }
        return ResultUtils.error(CodeMessage.JOIN_FAIL);
    }

    @GetMapping("/getFriendApplication")
    @ApiOperation("获得好友申请")
    public BaseResponse<List<User>> getFriendApplication(Long toUserId) {
        List<User> friendApplication = friendListService.getFriendApplication(toUserId);
        if (CollectionUtil.isEmpty(friendApplication)) {
            return ResultUtils.error(CodeMessage.SELECT_ERROR);
        }
        return ResultUtils.success(CodeMessage.SELECT_SUCCESS, friendApplication);
    }

    @GetMapping("/replyApplication")
    @ApiOperation("回复好友请求")
    public void replyApplication(Long num, Long fromUserId, Long toUserId) {
        friendListService.replyApplication(num, fromUserId, toUserId);
    }

    @GetMapping("/getFriendList")
    @ApiOperation("获取好友列表")
    public BaseResponse<FriendListVO> getFriendList(Long toUserId) {
        return ResultUtils.success(friendListService.getFriendList(toUserId));
    }

    @PostMapping("/sendMessageToUser")
    @ApiOperation("给好友发送信息")
    public void sendMessageToUser(@RequestBody SendMessageToUserDto messageDto) {
        messageService.sendMessageToUser(messageDto);
    }

    @PostMapping("/getUserMessage")
    @ApiOperation("获取与好友的全部聊天记录")
    public BaseResponse<List<Message>> getUserMessage(@RequestBody GetUserMessageDto messageDto) {
        List<Message> userMessage = messageService.getUserMessage(messageDto);
        if (CollectionUtil.isEmpty(userMessage)) {
            throw new BusinessException("聊天记录为空");
        }
        return ResultUtils.success(userMessage);
    }

    @GetMapping("/getUserLastMessage")
    @ApiOperation("获取与所有好友的最后一条聊天记录")
    public BaseResponse<MessageVO> getUserLastMessage(Long currentUserId) {
        MessageVO userLastMessage = messageService.getUserLastMessage(currentUserId);
        if (userLastMessage == null) {
            throw new BusinessException(CodeMessage.SELECT_ERROR, "数据为空");
        }
        return ResultUtils.success(userLastMessage);
    }

    @GetMapping("/deleteUser")
    @ApiOperation("删除好友")
    public void deleteUser(Long currentUserId, Long userId) {
        friendListService.deleteUser(currentUserId, userId);
    }
}
