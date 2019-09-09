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
         var pageCount,sum,curPageNum,dataList;
       var curPage = $("#curPageNum");
         var nextPage = $("#nextPage");
         var prePage =$("#prePage");
         var firstPage = $("#fistPage");
       var lastPage = $("#lastPage");
         $.get("/questionCount",function (data) {
             pageCount = parseInt(data.pageCount);
             sum = parseInt(data.sumInfoCount);
             pageList(pageCount,sum)
         })
       function pageList(pageCount,sum) {
           var curPageNum = parseInt($("#curPageNum").val());
           if (pageCount <= 5) {
               for (var i = 1; i <= pageCount; i++) {
                   nextPage.before('<li class="up" id="pageNum' + i + '"><a href="/questionPage?offset=' + i + '">' + i + '</a></li>');
               }
           } else {
               if (curPageNum <= 3) {
                   for (var i = 1; i <= 5; i++) {
                       nextPage.before('<li class="up" id="pageNum' + i + '"><a href="/questionPage?offset=' + i + '">' + i + '</a></li>');
                   }
               } else if (curPageNum + 2 <= pageCount) {
                   for (var i = curPageNum - 2; i <= curPageNum + 2; i++) {
                       nextPage.before('<li class="up" id="pageNum' + i + '"><a href="/questionPage?offset=' + i + '">' + i + '</a></li>');
                   }
               } else {
                   for (var i = pageCount - 4; i <= pageCount; i++) {
                       nextPage.before('<li class="up" id="pageNum' + i + '"><a href="/questionPage?offset=' + i + '">' + i + '</a></li>');
                   }
               }
           }
           var selectedPage = "pageNum" + curPageNum;
           $("#" + selectedPage).addClass("active").children().removeAttr("href");
           var prePageNum = parseInt($("#curPageNum").val()) - 1;
           var nextPageNum = parseInt($("#curPageNum").val()) + 1;

           var urlfirstPage = "/questionPage?offset=" + 1;
           firstPage.children().attr("href", urlfirstPage);
           var urllastPage = "/questionPage?offset=" + pageCount;
           lastPage.children().attr("href", urllastPage);
           if (prePageNum >= 1) {
               var urlprePage = "/questionPage?offset=" + prePageNum;
               prePage.children().attr("href", urlprePage);
           }
           if (nextPageNum <= pageCount){
               var urlnextPage = "/questionPage?offset=" + nextPageNum;
                 nextPage.children().attr("href", urlnextPage);
             }
       }
        });
})(window);