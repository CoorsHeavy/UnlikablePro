var aTags = document.getElementsByTagName("a");
var searchText = "View Profile";
var found;

for (var i = 0; i < aTags.length; i++) {
  if (aTags[i].textContent == searchText) {
    found = aTags[i];
    break;
  }
}
document.getElementsByClassName("link-profile")[0].addEventListener('click', function() { Android.showDialog(found.value) }, false);
javascript:function unlikable(){
    var elements = document.getElementsByClassName("mediaPhoto");
    var len = elements.length;
    for(var i = 0; i < len; i++) {
        elements[i].removeAttribute("data-reactid");
    }
    var elements = document.getElementsByClassName("timelineLikeButton");
    while(elements.length > 0){
        elements[0].parentNode.removeChild(elements[0]);
    }
}

javascript:unlikable();

var intervalID = window.setInterval(unlikable, 500);