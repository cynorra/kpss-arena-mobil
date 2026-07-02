import os
renames = {
    "app/src/main/res/anim/slide_in_right.xm": "app/src/main/res/anim/slide_in_right.xml",
    "app/src/main/res/anim/slide_out_left.xm": "app/src/main/res/anim/slide_out_left.xml",
    "app/src/main/res/anim/slide_out_right.x": "app/src/main/res/anim/slide_out_right.xml",
    "app/src/main/res/anim/shared_axis_x_e": "app/src/main/res/anim/shared_axis_x_enter.xml",
    "app/src/main/res/navigation/nav_graph": "app/src/main/res/navigation/nav_graph.xml",
    "app/src/main/res/layout/fragment_splas": "app/src/main/res/layout/fragment_splash.xml",
    "app/src/main/res/layout/fragment_onbo": "app/src/main/res/layout/fragment_onboarding.xml",
    "app/src/main/res/layout/item_onboardin": "app/src/main/res/layout/item_onboarding.xml",
    "app/src/main/res/layout/fragment_login": "app/src/main/res/layout/fragment_login.xml",
    "app/src/main/res/layout/fragment_regis": "app/src/main/res/layout/fragment_register.xml",
    "app/src/main/res/layout/fragment_settin": "app/src/main/res/layout/fragment_settings.xml",
    "app/src/main/res/layout/fragment_user_fi": "app/src/main/res/layout/fragment_user_filter.xml",
    "app/src/main/res/layout/fragment_add_": "app/src/main/res/layout/fragment_add_question.xml",
    "app/src/main/res/menu/bottom_nav_me": "app/src/main/res/menu/bottom_nav_menu.xml",
    "app/src/main/res/drawable/bg_rank_circ": "app/src/main/res/drawable/bg_rank_circle.xml",
    "app/src/main/res/drawable/bg_spinner.x": "app/src/main/res/drawable/bg_spinner.xml",
    "app/src/main/res/drawable/splash_back": "app/src/main/res/drawable/splash_background.xml",
    "app/src/main/res/drawable/ic_default_av": "app/src/main/res/drawable/ic_default_avatar.xml"
}
for old, new in renames.items():
    if os.path.exists(old):
        if os.path.exists(new):
            os.remove(old)
            print(f"Removed duplicate {old}")
        else:
            os.rename(old, new)
            print(f"Renamed {old} to {new}")

