function adjustHeaderTitle()
{
    $("#title").css("left", "calc( 50% - " + $("#title").width() / 2 + "px )");
    $("#title") .css("left", "-moz-calc( 50% - " + $("#title").width() / 2 + "px )");
    $("#title").css("left", "-webkit-calc( 50% - " + $("#title").width() / 2 + "px )");
};

$(document).ready(function()
{
    adjustHeaderTitle();
});