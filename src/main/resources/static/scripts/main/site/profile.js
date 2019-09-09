(function (window, undefined) {
    var Business = Base.getClass('main.util.Business');

    Base.ready({
        initialize: fInitialize
    });

    function fInitialize() {
        Business.followUser();
    }
    $(document).ready(function () {

        var pageCount,sum;
        var curPage = $("#curPageNum");
        var nextPage = $("#nextPage");
        var prePage =$("#prePage");
        var curUserId = $("#curId");
        var ulpage = $("#ulpage");
        var counturl = "/user/"+curUserId.val()+"/count";

        var pageurl = "/user/"+curUserId.val()+"/page?offset=";

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
})();