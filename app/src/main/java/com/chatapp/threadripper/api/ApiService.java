package com.chatapp.threadripper.api;

import com.chatapp.threadripper.authenticated.models.Contact;

import java.util.ArrayList;

public class ApiService {

    OnCompleteListener listener;

    public static ApiService getInstance() { return new ApiService(); }

    public ApiService() { }


    public void getContactsList(OnCompleteListener listener) {
        ArrayList<Contact> data = new ArrayList<>();
        String name[] = {"Laura Owens", "Angela Price", "Donald Turner", "Kelly", "Julia Harris", "Laura Owens", "Angela Price", "Donald Turner", "Kelly", "Julia Harris"};
        String img[] = {
                "https://kenh14cdn.com/2017/1-1506422137960.jpg",
                "https://znews-photo-td.zadn.vn/w1024/Uploaded/mdf_drezdw/2018_02_22/2.jpg",
                "https://znews-photo-td.zadn.vn/w1024/Uploaded/mdf_drezdw/2018_02_22/3.jpg",
                "https://cdn.pose.com.vn/assets/2018/03/10-hotgirl-viet-thong-linh-luong-follow-khung-tren-instagram-24d8053b-1.jpg",
                "https://dantricdn.com/thumb_w/640/2018/3/14/hot-girl-anh-the-huong-le-xe-buyt8-15210195344091865692058.jpg",
                "https://cdn1.iconfinder.com/data/icons/business-charts/512/customer-512.png",
                "http://abc.com/abc.jpg",
                "http://abc.com/abc.jpg",
                "http://2sao.vietnamnetjsc.vn/images/2017/07/31/06/54/hot-girl-xu-nghe-5.jpg",
                "http://abc.com/abc.jpg"
        };

        for (int i = 0; i < 10; i++) {
            Contact contact = new Contact();
            contact.setName(name[i]);
            contact.setImage(img[i]);
            data.add(contact);
        }

        if (listener != null)
            listener.onSuccess(data);
    }

    public interface OnCompleteListener {
        public void onSuccess(ArrayList<Contact> contactsList);
        public void onFailure(String message);
    }
}
