let stompClient = null;
let username;
let hedname;
let hedval;
let actualRicipient;


$(function () {
    username = $("#greeting").text();
    hedname = $("#headerName").val();
    hedval = $("#token").val();
    connect();
    $("#myPopup").toggle();
    $("#send_msg").click(function () {
        sendMessage();
    });
    $("#message").on('keyup', function (e) {
        if (e.keyCode === 13) {
            sendMessage();
        }
    });

    $("#sendInvitation").click(function (e) {
        sendInvitation(e);
    });
    $("#friendslogin").on('keyup', function (e) {
        if (e.keyCode === 13) {
            sendInvitation(e);
        }
    });

    $("body").click(function (e) {
        removeInivationsContainer(e);
    });
    $(".invitations").on('click', function () {
        showInvitationsContainer();
    });
});

function acceptFriend(index, name) {
    let data = "value=" + name;
    $.ajax({
        type: "post",
        beforeSend: function (request) {
            request.setRequestHeader(hedname, hedval);
        },
        url: '/invite/accept',
        data: data,
        success: function (data) {
            setTimeout(function () {
                $("." + index + "inv").remove();
            }, 1);
        },
        error: function (data) {
        }
    });
}

function deleteFriend(index, name) {
    let data = "value=" + name;
    $.ajax({
        type: "post",
        beforeSend: function (request) {
            request.setRequestHeader(hedname, hedval);
        },
        url: '/invite/delete',
        data: data,
        success: function (data) {
            setTimeout(function () {
                $("." + index + "inv").remove();
            }, 1);
        },
        error: function (data) {
        }
    });
}

function removeInivationsContainer(e) {

    if ($(e.target).closest(".invitations").length > 0 || $(e.target).closest(".invitations_content").length > 0) {
        return false;
    }
    let inv = $(".invitations");
    let invcon = $(".invitations_content");
    if (inv.hasClass("active")) {
        inv.toggleClass("active");
        invcon.css("max-height", 0);
        invcon.css("border-style", "none");
        invcon.css("overflow", "hidden");
    }
}

function showInvitationsContainer() {
    $(".invitations").toggleClass("active");
    let inv = $(".invitations_content");
    if (inv.css("max-height") > '0px') {
        inv.css("max-height", 0);
        inv.css("border-style", "none");
        inv.css("overflow", "hidden");


    } else {
        if (inv.prop('scrollHeight') > 500) {
            inv.css("max-height", "500px");
        } else if (inv.prop('scrollHeight') < 250) {
            inv.css("max-height", "250px");
        } else {
            inv.css("max-height", inv.prop('scrollHeight'));
        }
        inv.css("border-width", "1px");
        inv.css("border-color", "#c4c4c4");
        inv.css("border-style", "dashed solid solid solid");
    }
}

function sendInvitation(e) {
    e.preventDefault();
    $(".invitations_content").css("overflow", "visible");
    let dane = $("#friendslogin");
    $.ajax({
        type: "post",
        beforeSend: function (request) {
            request.setRequestHeader(hedname, hedval);
        },
        url: '/invite',
        data: dane.serialize(),
        success: function (data) {
            $("#myPopup").html(data);
            $("#myPopup").toggle(250);
            $("#sendInvitation").prop('disabled', true);
            setTimeout(function () {
                $("#myPopup").toggle(400);
                $("#sendInvitation").prop('disabled', false);
            }, 3000);
        },
        error: function (data) {
        }
    });
}

function connect() {
    let socket = new SockJS('/chat-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/topic/' + username, function (message) {
            showMessage(JSON.parse(message.body));
        });
        stompClient.subscribe('/topic/' + username + '?inv', function (message) {
            addInvitationBox(JSON.parse(message.body));
        });
        stompClient.subscribe('/topic/' + username + '?conv', function (message) {
            addFriendChatBox(JSON.parse(message.body));
        });
    });
}

function addFriendChatBox(message) {
    $(".inbox_chat").append('<div class="chat_list" onclick="setRecipent(\'' + message.username + '\', this);' +
        ' showMessages(\'' + message.username + '\')">\n' +
        '              <div class="chat_people" >\n' +
        '                <div class="chat_img">' +
        ' <img src="img/user-profile.png" alt="sunil"> </div>\n' +
        '                <div class="chat_ib">\n' +
        '                  <h5>' + message.username + '<span class="chat_date">Dec 25</span></h5>\n' +
        '                  <p>'+message.description+'</p>\n' +
        '                </div>\n' +
        '              </div>\n' +
        '            </div>'
    );
}

function addInvitationBox(message) {
    let invitationsSize = $(".inv_list_el").length;
    $(".invitations_list").append(' <div class = "' + invitationsSize + 'inv ' + ' inv_list_el"> \
        <a class="button " onclick="acceptFriend(\'' + invitationsSize + '\', \'' + message.value + '\')"> \
            <i class="fa fa-plus mr-1"></i> \
        </a> \
        <a class="button " onclick="deleteFriend(\'' + invitationsSize + '\', \'' + message.value + '\')"> \
        <i class="fa fa-minus mr-2"></i> \
        </a>\
        <span style="display:block; float:left; text-align: left">' + message.value + '</span>\
        <div style="clear: both"></div>\
        </div>\
    ');
}

function sendMessage() {
    let message = $("#message");
    if (actualRicipient == null) {
        return false;
    }
    if (message.val() === '') {
        return false;
    }
    let jsonData = JSON.stringify({'content': convertEmojiInMessage(message.val()), 'to': actualRicipient, 'from': username});
    stompClient.send("/app/czat", {}, jsonData);
    message.val('');
}

function showMessage(message) {
    let history = $(".msg_history");
    let historyContainer = $(".msg_history_container");
    if (history.height()-80 <= historyContainer.height() + historyContainer.scrollTop()) {
        if (message.from === username) {
            addIncomingMessage(message);
        } else {
            addOutgoingMessage(message);
        }
        historyContainer.animate({scrollTop: history.height()}, "slow");
    } else {
        if (message.from === username) {
            addIncomingMessage(message);
        } else {
            addOutgoingMessage(message);
        }
    }
}

let onClickAttrChat;

function setRecipent(friendName, object) {
    actualRicipient = friendName;
    $('.chat_list').each(function (i, obj) {
        if ($(obj).hasClass("active_chat")) {
            $(obj).attr("onclick", onClickAttrChat);
            $(obj).toggleClass("active_chat");
        }
    });
    onClickAttrChat = $(object).attr("onclick");
    object.attributes.onclick.nodeValue = "";
    $(object).toggleClass("active_chat");
}

function showMessages(friendName) {
    let msgHistory = $(".msg_history");
    let historyContainer = $(".msg_history_container");
    msgHistory.html("");
    $.ajax({
        type: "get",
        beforeSend: function (request) {
            request.setRequestHeader(hedname, hedval);
        },
        url: '/messages?user=' + friendName,
        success: function (data) {
            let jsonData = JSON.parse(data);
            for (i = 0; i < JSON.parse(data).length; i++) {
                let message = jsonData[i];
                if (message.from === username) {
                    addIncomingMessage(message);
                } else {
                    addOutgoingMessage(message);
                }
            }
            historyContainer.animate({scrollTop: msgHistory.height()}, "slow");
        },
        error: function (data) {
        }
    });
}

function addIncomingMessage(message) {
    let msgHistory = $(".msg_history");
    msgHistory.append('<div class="outgoing_msg">\n' +
        '                <div class="sent_msg">\n' +
        '                  <p>' + message.content + '</p>\n' +
        '                  <span class="time_date">' + message.date + '</span> </div>\n' +
        '              </div>');


}

function addOutgoingMessage(message) {
    let msgHistory = $(".msg_history");
    msgHistory.append('<div class="incoming_msg">\n' +
        '                <div class="incoming_msg_img"> <img src="https://ptetutorials.com/images/user-profile.png" alt="sunil"> </div>\n' +
        '                <div class="received_msg">\n' +
        '                  <div class="received_withd_msg">\n' +
        '                    <p>' + message.content + '</p>\n' +
        '                    <span class="time_date">' + message.date + '</span></div>\n' +
        '                </div>\n' +
        '              </div>');
}

function convertEmojiInMessage(message) {

    let mess = message.replace(":D", String.fromCodePoint(0x1F603));
    mess = mess.replace(":)", String.fromCodePoint(0x1F642));
    mess = mess.replace(";)", String.fromCodePoint(0x1F609));
    mess = mess.replace(":(", String.fromCodePoint(0x1F641));
    mess = mess.replace(";(", String.fromCodePoint(0x1F622));
    mess = mess.replace(":/", String.fromCodePoint(0x1F615));
    mess = mess.replace("xD", String.fromCodePoint(0x1F606));
    mess = mess.replace("<3", String.fromCodePoint(0x2764));
    mess = mess.replace(":P", String.fromCodePoint(0x1F61B));
    mess = mess.replace("8|", String.fromCodePoint(0x1F60E));
    return mess;
}