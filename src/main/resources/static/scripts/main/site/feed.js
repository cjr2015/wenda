(function (window, undefined) {
    var Action = Base.getClass('main.util.Action');
    var Business = Base.getClass('main.util.Business');

    Base.ready({
        initialize: fInitialize,
        // 事件代理
        events: {
            'click .js-like': fVote,
            'click .js-dislike': fVote
        }
    });

    function fInitialize() {
        var that = this;
        // 点击关注问题
        Business.followQuestion({
            countEl: $('.js-user-count'),
            listEl: $('.js-user-list')
        });
    }

    function fVote(oEvent) {
        var that = this;
        var oEl = $(oEvent.currentTarget);
        var oDv = oEl.closest('div.js-vote');
        var sId = $.trim(oDv.attr('data-id'));
        var bLike = oEl.hasClass('js-like');
        if (!sId) {
            return;
        }
        if (that.isVote) {
            return;
        }
        that.isVote = true;
        Action[bLike ? 'like' : 'dislike']({
            commentId: sId,
            call: function (oResult) {
                // 调整样式
                oDv.find('.pressed').removeClass('pressed');
                oDv.find(bLike ? '.js-like' : '.js-dislike').addClass('pressed');
                // 更新数量
                oDv.closest('div.js-comment').find('span.js-voteCount').html(oResult.msg);
            },
            error: function (oResult) {
                if (oResult.code === 999) {
                    alert('请登录后再操作');
                    window.location.href = '/reglogin?next=' + window.decodeURIComponent(window.location.href);
                } else {
                    alert('出现错误，请重试');
                }
            },
            always: function () {
                that.isVote = false;
            }
        });
    }

    function fUnlike(oEvent) {
        var that = this;
        var oEl = $(oEvent.currentTarget);

    }
    $(document).ready(function () {

        var pageCount,sum;
        var curPage = $("#curPageNum");
        var nextPage = $("#nextPage");
        var prePage =$("#prePage");
        var curUserId = $("#curId");
        var ulpage = $("#ulpage");
        var counturl = "/user/"+curUserId.val()+"/followeesCount";

        var pageurl = "/user/"+curUserId.val()+"/followeesPage?offset=";

        $.get(counturl,function (data) {
            pageCount = parseInt(data.pageCount);
            sum = parseInt(data.sumInfoCount);
            // alert(pageCount);
            // alert(sum);
            pageList(pageCount,sum)
            if(sum<=0){
                ulpage.addClass("hidden");
            }
        })
        function pageList(pageCount,sum) {

            for (var i = 1; i <= pageCount; i++) {
                if (i <= 5) {
                    nextPage.before('<li id="pageNum' + i + '"><a href="'+pageurl+i+'">' + i + '</a></li>');
                } else {
                    nextPage.before('<li class="hidden" id="pageNum' + i + '"><a href="'+pageurl+i+'">' + i + '</a></li>');
                }
            }
            var curPageNum =parseInt($("#curPageNum").val());
            var selectedPage = "pageNum"+curPageNum;

            $("#"+selectedPage).addClass("active");
            var prePageNum = parseInt($("#curPageNum").val()) - 1;
            var nextPageNum = parseInt($("#curPageNum").val()) + 1;
            if(curPageNum>5){

                var page1 = "pageNum"+(curPageNum-5);
                $("#"+page1).addClass("hidden");
                var page2 = "pageNum"+(curPageNum);
                $("#"+page2).removeClass("hidden")
            }
            nextPage.children().on("click", function () {
                if(nextPageNum<=pageCount) {

                    var url = pageurl + nextPageNum;
                    window.location.href = url;
                }
            })

            prePage.children().on("click", function () {
                if(prePageNum>=1) {
                    var url = pageurl + prePageNum;

                    window.location.href = url;
                }
            })
        }
    });
})(window);