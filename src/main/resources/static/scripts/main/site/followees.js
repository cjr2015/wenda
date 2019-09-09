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
        var firstPage = $("#fistPage");
        var lastPage = $("#lastPage");
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

            var curPageNum =parseInt($("#curPageNum").val());
            if(pageCount<=5){
                for (var i = 1; i <= pageCount; i++) {
                    nextPage.before('<li class="up" id="pageNum' + i + '"><a href="'+pageurl+i+'">' + i + '</a></li>');
                }
            }else {
                if(curPageNum<=3){
                    for (var i = 1; i <= 5; i++) {
                        nextPage.before('<li class="up" id="pageNum' + i + '"><a href="'+pageurl+i+'">' + i + '</a></li>');
                    }
                }else if(curPageNum+2<=pageCount){
                    for (var i = curPageNum-2; i <= curPageNum+2; i++) {
                        nextPage.before('<li class="up" id="pageNum' + i + '"><a href="'+pageurl+i+'">' + i + '</a></li>');
                    }
                }else {
                    for (var i = pageCount-4; i <= pageCount; i++) {
                        nextPage.before('<li class="up" id="pageNum' + i + '"><a href="'+pageurl+i+'">' + i + '</a></li>');
                    }
                }
            }
            var selectedPage = "pageNum"+curPageNum;
            $("#"+selectedPage).addClass("active").children().removeAttr("href");
            var prePageNum = parseInt($("#curPageNum").val()) - 1;
            var nextPageNum = parseInt($("#curPageNum").val()) + 1;


            var urlfirstPage = pageurl + 1;
            firstPage.children().attr("href", urlfirstPage);
            var urllastPage = pageurl + pageCount;
            lastPage.children().attr("href",urllastPage);
            var urlprePage = pageurl + prePageNum;
            if(prePageNum>=1) {
                prePage.children().attr("href", urlprePage);
            }
            var urlnextPage = pageurl + nextPageNum;
            if(nextPageNum<=pageCount) {
                nextPage.children().attr("href", urlnextPage);
            }
        }
    });
})();