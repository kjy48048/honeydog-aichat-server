//alert(document.title);
// websocket & stomp initialize
let room = {
    init: function () {
        let _this = this;

        let protocol = window.location.protocol === "https:" ? "wss:" : "ws:";
        let wsHost = window.location.host; // 호스트 이름과 포트 번호 (있는 경우)
        let wsPath = "/ws/app/chat"; // WebSocket 서버의 경로
        _this.wsUrl = new WebSocket(protocol + "//" + wsHost + wsPath);

        _this.ws = Stomp.over(_this.wsUrl);

        _this.reconnect = 0;
        _this.roomUuid = $("#roomUuid").val();
        _this.userUuid = $("#userUuid").val();
        _this.roomUserUuid = $("#roomUserUuid").val();
        _this.sender = $("#sender").val();

        $('#btn-message').on('click', function () {
            _this.sendMessage();
        });

        $('#ipt-message').on('keypress', function (e) {
            if(e.which === 13) _this.sendMessage();
        });
        $('#btn-delete').on('click', function () {
            $.ajax({
                type: 'PUT',
                url: '/api/v2/app/room/out-room/room-id/' + _this.roomUuid,
                dataType: 'json',
                contentType: 'application/json; charset=utf-8'
            }).done(function () {
                alert('채팅방에서 나갔습니다.');
                window.location.href = '/lobby';
            }).fail(function (error) {
                alert('채팅방 나가는 것에 실패하였습니다.')
                console.log(JSON.stringify(error));
            })
        });

        _this.created();
        _this.connect();
    },
    connect: function(){
        let _this = this;

        _this.ws.connect({}, function(frame) {
            _this.ws.subscribe("/topic/chat/room/"+_this.roomUuid, function(message) {
                let recv = JSON.parse(message.body);
                _this.recvMessage(recv);
            });
            _this.ws.send("/app/chat/v2/message", {},
                JSON.stringify({
                    messageType:'ENTER',
                    roomUuid:_this.roomUuid,
                    nick:_this.sender
                }
            ));
        }, function(error) {
            if(_this.reconnect++ <= 5) {
                setTimeout(function() {
                    console.log("connection reconnect");
                    _this.sock = new SockJS("/ws/app/chat");
                    _this.ws = Stomp.over(_this.sock);
                    _this.connect();
                },10*1000);
            }
        });
    },
    created: function(){
        let _this = this;
        _this.findRoom();
    },
    findRoom: function() {
        let _this = this;
        $.ajax({
            type: 'GET',
            url: '/api/v2/app/room/room-id' + _this.roomUuid,
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
        }).done(function (response) {
            console.log(JSON.stringify(response));
        }).fail(function (error) {
            console.log(JSON.stringify(error));
        })
    },
    sendMessage: function() {
        let _this = this;
        let message = $("#ipt-message").val();

        if(!message) {
            return;
        }

        _this.handleSendMessage('TALK', _this.roomUuid, _this.roomUserUuid, message);

        $("#ipt-message").val('');
    },
    recvMessage: function(recv) {
        let _this = this;

        let messageUl = $("#messageUl");

        console.log("recvMessage..." + recv);

        let sender = recv.type === "TALK" ? recv.sender : "[" + recv.sender + "]";
        let modifiedDate = "(" + recv.formattedModifiedDate + ")";
        let className = 'list-group-item';

        if(""+_this.roomUserUuid === ""+recv.roomUserUuid) {
            className += ' recv-my';
        } else if(recv.type !== "TALK") {
            className += ' recv-system';
        } else {
            className += ' recv-other';
        }

        let message = "<li class='"+className+"'>";
        message += sender + ": " + recv.message + modifiedDate;
        message += "</li>";

        messageUl.append(message);

        // 맨밑으로
        messageUl.scrollTop(messageUl[0].scrollHeight);
    },
    handleSendMessage: function(messageTypeStr, roomUuid, roomUserUuid, message) {
        let _this = this;
        _this.ws.send("/app/chat/v2/message", {},
            JSON.stringify({
                    messageType:messageTypeStr,
                    message:message,
                    nick:_this.sender,
                    roomUuid:roomUuid,
                    userUuid:_this.userUuid,
                }
            )
        );
    }
};
window.onload = function() {
    room.init();
}

//todo: 채팅방 나가기 구현, 채팅방 참여자 보여주기 구현...
