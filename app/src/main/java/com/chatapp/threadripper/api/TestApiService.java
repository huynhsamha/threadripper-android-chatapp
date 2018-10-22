package com.chatapp.threadripper.api;


import com.chatapp.threadripper.models.User;

import java.util.ArrayList;

public class TestApiService {

    public static TestApiService getInstance() { return new TestApiService(); }

    public interface OnCompleteListener {
        void onSuccess(ArrayList list);
        void onFailure(String errorMessage);
    }

    public void getUsersList(OnCompleteListener listener) {

        ArrayList<User> data = new ArrayList<>();

        String usernames[] = {
                "neil",
                "kristen",
                "avery",
                "sophie",
                "katie",
                "nelson",
                "tim",
                "deann",
                "claudia",
                "elijah",
                "gail",
                "arthur",
                "olivia"
        };
        String emails[] = {
                "neil.perkins65@example.com",
                "kristen.myers27@example.com",
                "avery.barrett37@example.com",
                "sophie.clark48@example.com",
                "katie.jackson66@example.com",
                "nelson.simmons45@example.com",
                "tim.garza17@example.com",
                "deann.jennings64@example.com",
                "claudia.jacobs45@example.com",
                "elijah.howell34@example.com",
                "gail.moore42@example.com",
                "arthur.stephens90@example.com",
                "olivia.bell24@example.com"
        };
        String displayNames[] = {
                "Neil Perkins",
                "Kristen Myers",
                "Avery Barrett",
                "Sophie Clark",
                "Katie Jackson",
                "Nelson Simmons",
                "Claudia Howell",
                "Tim Garza",
                "Deann Jennings",
                "Claudia Jacobs",
                "Elijah Howell",
                "Gail Moore",
                "Arthur Stephens",
                "Olivia Bell"
        };
        String photoUrls[] = {
                "http://2sao.vietnamnetjsc.vn/2016/07/01/23/15/xtm1a.jpg",
                "http://abc.com/abc.jpg",
                "https://znews-photo-td.zadn.vn/w660/Uploaded/bpivpjbp/2018_08_20/mpen180803MB002__1.jpg",
                "http://nguoi-noi-tieng.com/photo/tieu-su-dien-vien-xa-thi-man-6850.jpg",
                "https://dantricdn.com/thumb_w/640/2018/3/14/hot-girl-anh-the-huong-le-xe-buyt8-15210195344091865692058.jpg",
                "https://znews-photo-td.zadn.vn/w1024/Uploaded/mdf_drezdw/2018_02_22/3.jpg",
                "https://2sao.vietnamnetjsc.vn/images/2018/07/17/11/43/xa-thi-man-4.jpg",
                "http://abc.com/abc.jpg",
                "https://kenh14cdn.com/2017/1-1506422137960.jpg",
                "https://cdn.iconscout.com/icon/free/png-256/avatar-369-456321.png",
                "https://dantricdn.com/thumb_w/640/2018/3/14/hot-girl-anh-the-huong-le-xe-buyt8-15210195344091865692058.jpg",
                "https://znews-photo-td.zadn.vn/w1024/Uploaded/mdf_drezdw/2018_02_22/2.jpg",
                "https://cdn1.iconfinder.com/data/icons/business-charts/512/customer-512.png"
        };

        for (int i = 0; i < usernames.length; i++) {
            User user = new User(usernames[i], emails[i], null, displayNames[i], photoUrls[i]);
            data.add(user);
        }

        if (listener != null) listener.onSuccess(data);
    }

}
