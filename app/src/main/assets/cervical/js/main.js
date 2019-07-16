/* ========================================================================= */
/*  Preloader Script
/* =========================================================================*/

window.onload = function () {
    document.getElementById('loading-mask').style.display = 'none';
} 
//preloader

$(window).load(function(){

$(".preloader").delay(1000).fadeOut("slow")

});
/* ----------------------------------------------------------- */
    /*   SCROLL TOP BUTTON
    /* ----------------------------------------------------------- */

    //Check to see if the window is top if not then display button

      $(window).scroll(function(){
        if ($(this).scrollTop() > 100) {
          $('.scrollToTop').fadeIn();
        } else {
          $('.scrollToTop').fadeOut();
        }
      });
       
      //Click event to scroll to top

      $('.scrollToTop').click(function(){
        $('html, body').animate({scrollTop : 0},800);
        return false;
      });

   
    /* ========================================================================= */
    /*  Menu item highlighting
    /* ========================================================================= */

 $(function(){
    jQuery(window).scroll(function () {
        if (jQuery(window).scrollTop() > 100) {
            jQuery("#navigation").css("background-color","#228B22");
            jQuery("#navigation").addClass("animated-nav");
        } else {
            jQuery("#navigation").css("background-color","transparent");
            jQuery("#navigation").removeClass("animated-nav");
            
        }
    });

    $('#nav').onePageNav({
        filter: ':not(.external)',
        scrollSpeed: 950,
        scrollThreshold: 1
    });
 });
