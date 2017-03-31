import $ from 'jquery';
import Component from '../lib/Component';

export default class SidebarToggle extends Component {
    static get id() {
        return 'SidebarToggle';
    }

    init() {
        super.init();
        this.$node.on('click', function (e) {
            e.preventDefault();

            //If window is small enough, enable sidebar push menu
            if ($(window).width() <= 992) {
                $('.row-offcanvas').toggleClass('active');
                $('.left-side').removeClass("collapse-left");
                $(".right-side").removeClass("strech");
                $('.row-offcanvas').toggleClass("relative");
            } else {
                //Else, enable content streching
                $('.left-side').toggleClass("collapse-left");
                $(".right-side").toggleClass("strech");
            }
        });

    }

}
