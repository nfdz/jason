/**
 *  Jason Snippets 2016
 */

// is closed by default
var isOpen = false;

function openNav() {
    document.getElementById("mySidenav").style.width = "20em";
    //document.getElementById("main").style.marginLeft = "20em";
    //document.getElementById("main").style.backgroundColor = "rgba(0,0,0,0.4)";
}

function closeNav() {
    document.getElementById("mySidenav").style.width = "0";
    //document.getElementById("main").style.marginLeft= "0";
    //document.getElementById("main").style.backgroundColor = "white";
}

function toggleNav() {
  if (isOpen) {
    closeNav();
    isOpen = false;
  } else {
    openNav();
    isOpen = true;
  }
}
