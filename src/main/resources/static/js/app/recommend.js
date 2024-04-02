let main = {
    init : function () {
        let _this = this;
        $('#btn-save').on('click', function () {
          _this.save();
        });
        $('#btn-update').on('click', function () {
            _this.update();
        });
        $('#btn-delete').on('click', function () {
            _this.delete();
        });
    },
    save : function () {
        let data = {
            nick: $('#nick').val(),
            question: $('#question').val(),
            color: $('#color').val(),
            orderIndex: $('#orderIndex').val()
        };

        $.ajax({
            type: 'POST',
            url: '/api/v2/app/recommend/',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function () {
            alert('글이 등록되었습니다.');
            window.location.href = '/recommend';
        }).fail(function (error) {
            alert(JSON.stringify(error));
        })
    },
    update: function () {
        let data = {
            nick: $('#nick').val(),
            question: $('#question').val(),
            color: $('#color').val(),
            orderIndex: $('#orderIndex').val()
        };

        let id = $('#id').val();

        $.ajax({
            type: 'PUT',
            url: '/api/v2/app/recommend/' + id,
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function () {
            alert('글이 수정되었습니다.');
            window.location.href = '/recommend';
        }).fail(function (error) {
            alert(JSON.stringify(error));
        })
    },
    delete: function () {
        let id = $('#id').val();

        $.ajax({
            type: 'DELETE',
            url: '/api/v2/app/recommend/' + id,
            dataType: 'json',
            contentType: 'application/json; charset=utf-8'
        }).done(function () {
            alert('글이 삭제되었습니다.');
            window.location.href = '/recommend';
        }).fail(function (error) {
            alert(JSON.stringify(error));
        })
    }
};
window.onload = function() {
    main.init();
}
