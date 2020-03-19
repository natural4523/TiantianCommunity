/**
 * 提交一级评论
 */
function post() {
    var questionId = $("#question_id").val();
    var content = $("#comment_content").val();
    comment2target(questionId,1,content);
}


/**
 * 展开二级评论
 */
function collapseComments(e) {
    var id = e.getAttribute("data-id");
    var comments = $("#comment-" + id);

    console.log(comments);
    // 获取一下二级评论的展开状态
    var collapse = e.getAttribute("data-collapse");
    if (collapse) {
        // 折叠二级评论
        comments.removeClass("in");
        e.removeAttribute("data-collapse");
        e.classList.remove("active");
    } else {
        var subCommentContainer = $("#comment-" + id);
        if(subCommentContainer.children().length != 1){
            //展开二级评论
            comments.addClass("in");
            // 标记二级评论展开状态
            e.setAttribute("data-collapse", "in");
            e.classList.add("active");
        }else{
            $.getJSON("/comment/" + id, function (data) {
                $.each(data.data.reverse(), function (index, comment) {
                    var mediaLeftElement = $("<div/>",{
                        "class":"media-left"
                    }).append($("<img/>", {
                        "class": "media-object img-rounded",
                        "src": comment.user.avatarUrl
                    }));

                    var mediaBodyElement = $("<div/>",{
                        "class":"media-body"
                    }).append($("<h5/>", {
                        "class": "media-heading",
                        "html": comment.user.name
                    })).append($("<div/>", {
                        "html": comment.content
                    })).append($("<div/>", {
                        "class": "menu"
                    }).append($("<span/>", {
                        "class": "pull-right",
                        "html": moment(comment.gmtCreate).format('YYYY-MM-DD')
                    })))

                    var mediaElement = $("<div/>",{
                        "class":"media"
                    }).append(mediaLeftElement).append(mediaBodyElement);

                    var commentElement = $("<div/>",{
                        "class":"col-lg-12 col-md-12 col-sm-12 col-xs-12 comments"
                    }).append(mediaElement);

                    //追加到container上
                    subCommentContainer.prepend(commentElement);
                });

                //展开二级评论
                comments.addClass("in");
                // 标记二级评论展开状态
                e.setAttribute("data-collapse", "in");
                e.classList.add("active");
            });
        }

    }
}

/**
 * 提交二级评论
 * @param e
 */
function comment(e) {
    var commentId = e.getAttribute("data-id");
    var content = $("#input-"+commentId).val();
    comment2target(commentId,2,content);
}

/**
 * 一级、二级评论的ajax
 * @param targetId
 * @param type
 * @param content
 */
function comment2target(targetId,type,content){
    if (!content) {
        alert("不能回复空内容！");
        return;
    }

    var userId = $('#userId').val();
    var userState = 0;
    $.ajax({
        type: "get",
        url: "/findById/" + userId,
        contentType: "application/json",
        dataType: "json",
        success: function (data) {
            userState = data.state;
            if (userState == 0) {
                alert("您的账号已被封禁，如有误封，请联系管理员！");
                return;
            }else {
                $.ajax({
                    type: "post",
                    url: "/comment",
                    contentType: "application/json",
                    data: JSON.stringify({
                        "parentId": targetId,
                        "content": content,
                        "type": type
                    }),
                    dataType: "json",
                    success: function (response) {
                        if (response.code == 200) {
                            window.location.reload();
                        } else {
                            if (response.code == 2003) {
                                var isAccepted = confirm(response.message);
                                if (isAccepted) {
                                    window.open("/toLogin");
                                    window.localStorage.setItem("closable", true);
                                }
                            }
                            alert(response.message);
                        }
                        console.log(response)
                    }
                })
            }
        }
    })




}

/**
 * 展示标签
 */
function showSelectTag() {
    $("#select-tag").show();
}

/**
 * 判断添加的标签是否出现过
 * @param e
 */
function selectTag(e) {
    var value = e.getAttribute("data-tag");
    var previous = $("#tag").val();

    /*判断添加的标签是否出现过*/
    if (previous.indexOf(value) == -1){
        /*假如之前添加过标签*/
        if (previous){
            $("#tag").val(previous + '，' + value);
        }else{
            $("#tag").val(value);
        }
    }
}

/**
 * 封禁用户和解封用户(问题界面的按钮)
 * @param e
 */
function freezeOrUnfreeze(e) {
    var userId = e.getAttribute("data-id");
    var userState = 0;

    $.ajax({
        type: "get",
        url: "/findById/" + userId,
        contentType: "application/json",
        /*data: {
            "userId": userId
        },*/
        dataType: "json",
        success: function (data) {
            userState = data.state;
            console.log("当前用户状态:" + userState)
            if (userState == 1){
                $.ajax({
                    type: "get",
                    url: "/freeze/" + userId,
                    contentType: "application/json",
                    /*data: {
                        "userId": userId
                    },*/
                    dataType: "json",
                    success: function (data) {
                        userState = data.state;
                        console.log("处理之后用户状态:" +userState)
                    }

                })
                $('#stateButton').text("解封该用户")
            }else if (userState == 0){
                $.ajax({
                    type: "get",
                    url: "/unfreeze/" + userId,
                    contentType: "application/json",
                    /*data: {
                        "userId": userId
                    },*/
                    dataType: "json",
                    success: function (data) {
                        userState = data.state;
                        console.log("处理之后用户状态:" +userState)
                    }
                })
                $('#stateButton').text("封禁该用户")
            }
        }
    })
}

function publishError() {
    var title = $('#title').val();
    var description = $('#description').val();
    var tag = $('#tag').val();
    var userState = $('#userState').val();

    if (title == ''){
        alert('标题不能为空！');
        return false;
    }
    if (description == ''){
        alert('问题描述不能为空！');
        return false;
    }
    if (tag == ''){
        alert('标签不能为空！');
        return false;
    }
    if (userState == 0){
        alert('您的账号已被封禁，如有误封，请联系管理员！')
        return false;
    }
}

function unfreeze(e) {
    var userId = e.getAttribute("data-id");
    var userState = 0;

    $.ajax({
        type: "get",
        url: "/unfreeze/" + userId,
        contentType: "application/json",
        dataType: "json",
        success: function (data) {
            userState = data.state;
            console.log("处理之后用户状态:" +userState)
        }
    })
    window.location.reload();

}

function thumbsUp(e) {
    var id = e.getAttribute("data-id");

    $.ajax({
        type: "get",
        url: "/thumbsUp/" + id,
        contentType: "application/json",
        dataType: "json",
        success: function (data) {
        }
    })
    window.location.reload();

}

/*
var flag = false
function checkLogin() {
    var username = $("#username").val();
    var password = $("#password").val();
    $.ajax({
        type: "get",
        url: "/checkUsername/" + username,
        contentType: "application/json",
        dataType: "json",
        success: function (data) {
            if (data == false) {
                $.ajax({
                    type: "get",
                    url: "/checkLogin/" + username,
                    contentType: "application/json",
                    dataType: "json",
                    success: function (data) {
                        if (data.password != password){
                            alert("账号或密码错误！")
                            console.log(111)
                            flag = false
                        } else if (data.state == 0) {
                            alert("该用户已被冻结！")
                            //window.location.reload()
                            flag = false
                        }else if (data.password == password) {
                            flag = true
                            //console.log(flag)
                        }8
                    }
                })
            }else {
                alert("该用户不存在，请重新输入用户名！")
                //window.location.reload()
                flag = false
            }
        }
    })
    console.log(flag)
    return flag
}*/
