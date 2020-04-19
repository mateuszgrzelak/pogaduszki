let stompClient = null;
let username;
let hedname;
let hedval;
let actualRicipient;


$(function () {
    username = $( "#greeting" ).text();
    hedname = $("#headerName").val();
    hedval = $("#token").val();
    connect();
    $( "#myPopup" ).toggle();
    $( "#send_msg" ).click(function() { sendMessage(); });
    $( "#sendInvitation" ).click(function(e){sendInvitation(e);});
    $( "body" ).click(function(e){removeInivationsContainer(e);});
    $(".invitations").on('click', function(){showInvitationsContainer();});
});

function acceptFriend(index, name){
    let data = "value="+name;
    $.ajax({
        type: "post",
        beforeSend: function(request) {
            request.setRequestHeader(hedname, hedval);
        },
        url: '/invite/accept',
        data: data,
        success: function(data) {
            setTimeout(function() {$("."+index+"inv").remove();
            }, 1);
            showFriendChatBox(name);
        },
        error: function (data) {
        }
    });
}

function deleteFriend(index, name){
    let data = "value="+name;
    $.ajax({
        type: "post",
        beforeSend: function(request) {
            request.setRequestHeader(hedname, hedval);
        },
        url: '/invite/delete',
        data: data,
        success: function(data)
        {
            setTimeout(function() {$("."+index+"inv").remove();
            }, 1);
        },
        error: function (data) {
        }
    });
}

function removeInivationsContainer(e){

    if( $(e.target).closest(".invitations").length > 0 || $(e.target).closest(".invitations_content").length > 0) {
        return false;
    }
    let inv = $(".invitations");
    let invcon = $(".invitations_content");
    if(inv.hasClass("active")){
        inv.toggleClass("active");
        invcon.css("max-height", 0);
        invcon.css("border-style", "none");
        invcon.css("overflow", "hidden");
    }
}

function showInvitationsContainer(){
    $(".invitations").toggleClass("active");
    let inv = $(".invitations_content");
    if(inv.css("max-height")>'0px'){
        inv.css("max-height", 0);
        inv.css("border-style", "none");
        inv.css("overflow", "hidden");


    }else{
        if(inv.prop('scrollHeight')>500){
            inv.css("max-height", "500px");
        }else if(inv.prop('scrollHeight')<250){
            inv.css("max-height", "250px");
        }else{
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
        beforeSend: function(request) {
            request.setRequestHeader(hedname, hedval);
        },
        url: '/invite',
        data: dane.serialize(),
        success: function(data)
        {
            $("#myPopup").html(data);
            $( "#myPopup" ).toggle(250);
            $("#sendInvitation").prop('disabled', true);
            setTimeout(function() {$( "#myPopup" ).toggle(400);
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
            showInvitation(JSON.parse(message.body));
        });
        stompClient.subscribe('/topic/' + username + '?conv', function (message) {
            showFriendChatBox(JSON.parse(message.body).value);
        });
    });
}

function showFriendChatBox(message){
    $(".inbox_chat").append('<div class="chat_list active_chat" onclick="setRecipent('+message+');' +
        ' showMessages('+message+')">\n' +
        '              <div class="chat_people" >\n' +
        '                <div class="chat_img">' +
        ' <img src="https://ptetutorials.com/images/user-profile.png" alt="sunil"> </div>\n' +
        '                <div class="chat_ib">\n' +
        '                  <h5>'+message+'<span class="chat_date">Dec 25</span></h5>\n' +
        '                  <p>Test, which is a new approach to have all solutions\n' +
        '                    astrology under one roof.</p>\n' +
        '                </div>\n' +
        '              </div>\n' +
        '            </div>'
    );
}

function showInvitation(message){
    let invitationsSize = $(".inv_list_el").length;
    $(".invitations_list").append(' <div class = "'+ invitationsSize+'inv ' +' inv_list_el"> \
        <a class="button " onclick="acceptFriend(\''+invitationsSize+'\', \''+message.value+'\')"> \
            <i class="fa fa-plus mr-1"></i> \
        </a> \
        <a class="button " onclick="deleteFriend(\''+invitationsSize+'\', \''+message.value+'\')"> \
        <i class="fa fa-minus mr-2"></i> \
        </a>\
        <span style="display:block; float:left; text-align: left">'+message.value+'</span>\
        <div style="clear: both"></div>\
        </div>\
    ');


}

// function disconnect() {
//     if (stompClient !== null) {
//         stompClient.disconnect();
//     }
//     setConnected(false);
//     console.log("Disconnected");
// }

function sendMessage() {
    if (actualRicipient==null){
        return false;
    }
    let jsonData = JSON.stringify({'content': $("#message").val(),'to':actualRicipient, 'from':username});
    stompClient.send("/app/czat", {}, jsonData);
}

function showMessage(message) {
    $(".msg_history").append("<div>"+message.content+"</div>");
}

function setRecipent(friendName){
    actualRicipient = friendName;
}