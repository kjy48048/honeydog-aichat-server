let lobby = {
    init: function () {
        let _this = this;
        $('#btn-create').on('click', function () {
            _this.createRoom();
        });

        $('#title').on('keypress', function (e) {
            if(e.which === 13) _this.createRoom();
        })

        _this.created();
    },
    created: function () {
        this.findSearchRoom();
    },
    findSearchRoom: function(_pageNumber) {
        let _this = this;

        let params = $.param({
            roomNick: '',
            roomStatus: '',
            roomType: '',
            pageNumber: _pageNumber,
            pageSize: 10
        });

        $.ajax({
            type: 'GET',
            url: '/api/v2/app/room/list/search?' + params,
            dataType: 'json'
        }).done(function (response) {
            $("#room-list").empty();
            for(let room of response.roomList) {
                let tr = "<tr id='room"+room.roomUuid+"'>";
                tr += "<td>"+room.roomUuid+"</td>";
                tr += "<td>"+room.roomNick+"</td>";
                tr += "<td>"+room.roomStatus+"</td>";
                tr += "<td>"+room.roomType+"</td>";
                tr += "<td>"+room.formattedModifiedDate+"</td>";
                tr += "<tr>";

                $("#room-list").append(tr);
                if(room.roomStatus === 'OPEN') {
                    $("#room"+room.roomUuid).on("click", function () {
                        _this.enterRoom(room.roomUuid);
                    })
                }
            }

            // 페이징 네비게이션 생성
            let $pagination = $('#pagination');
            $pagination.empty(); // 기존 페이지네이션 제거
            for(let i = 0; i < response.totalPages; i++) {
                let li = $("<li class='page-item " + (response.pageNumber === i ? "active" : "") + "'>" +
                    "<a class='page-link' href='#'>" + (i + 1) + "</a></li>");
                li.on("click", function(e) {
                    e.preventDefault();
                    _this.findSearchRoom(i);
                });
                $pagination.append(li);
            }

        }).fail(function (error) {
            console.log(JSON.stringify(error));
        })
    },
    createRoom: function() {
        let titleName = $('#title').val();
        if("" === titleName) {
            alert("방 제목을 입력해 주십시요.");
        } else {
            let data = {
                roomNick: titleName,
                userUuid: $("#userId").val(),
                friendEmails: "",
                aiChatter: "",
                roomStatus: "OPEN",
                roomType: "AI"
            };

            $.ajax({
                type: 'POST',
                url: '/api/v2/app/room',
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                data: JSON.stringify(data)
            }).done(function (response) {
                console.log(JSON.stringify(response));
                alert("채팅방 개설에 성공하였습니다.");
                window.location.href = '/room/' + response;
            }).fail(function (error) {
                console.log(JSON.stringify(error));
                alert("채팅방 개설에 실패하였습니다.");
            })
        }
    },
    enterRoom: function(roomUuid) {
        location.href="/room/"+roomUuid;
    }
};

window.onload = function() {
    lobby.init();
}
