package com.chatapp.threadripper.api;

import android.util.Log;

import com.chatapp.threadripper.authenticated.models.Contact;
import com.chatapp.threadripper.authenticated.models.Message;
import com.chatapp.threadripper.authenticated.models.MessagesChat;
import com.chatapp.threadripper.models.User;
import com.chatapp.threadripper.utils.Constants;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TestApiService {

    public static TestApiService getInstance() { return new TestApiService(); }

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public interface OnCompleteListener {
        void onSuccess(ArrayList list);
        void onFailure(String errorMessage);
    }

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

        if (listener != null) listener.onSuccess(data);
    }

    public void getMessagesChatList(OnCompleteListener listener) {
        ArrayList<MessagesChat> data = new ArrayList<>();
        String name[] = {"Laura Owens", "Angela Price", "Donald Turner", "Kelly", "Julia Harris", "Laura Owens", "Angela Price", "Donald Turner", "Kelly", "Julia Harris"};
        String lastchat[] = {"Hi Laura Owens", "Hi there how are you", "Can we meet?", "Ow this awesome", "How are you?", "Ow this awesome", "How are you?", "Ow this awesome", "How are you?", "How are you?"};
        boolean online[] = {true, false, true, false, true, true, true, false, false, true};
        String img[] = {
                "http://2sao.vietnamnetjsc.vn/2016/07/01/23/15/xtm1a.jpg",
                "https://znews-photo-td.zadn.vn/w660/Uploaded/bpivpjbp/2018_08_20/mpen180803MB002__1.jpg",
                "http://nguoi-noi-tieng.com/photo/tieu-su-dien-vien-xa-thi-man-6850.jpg",
                "https://2sao.vietnamnetjsc.vn/images/2018/07/17/11/43/xa-thi-man-4.jpg",
                "https://cdn.iconscout.com/icon/free/png-256/avatar-369-456321.png",
                "https://cdn1.iconfinder.com/data/icons/business-charts/512/customer-512.png",
                "http://abc.com/abc.jpg",
                "http://abc.com/abc.jpg",
                "http://www.asiaone.com/sites/default/files/original_images/Sep2014/170914_charmaine_lollipop.jpgg",
                "http://abc.com/abc.jpg"
        };

        for (int i = 0; i < 10; i++) {
            MessagesChat messagesChat = new MessagesChat();
            messagesChat.setTime("5:04pm");
            messagesChat.setName(name[i]);
            messagesChat.setImage(img[i]);
            messagesChat.setOnline(online[i]);
            messagesChat.setLastChat(lastchat[i]);
            data.add(messagesChat);
        }

        if (listener != null) listener.onSuccess(data);
    }

    public void getMessages(OnCompleteListener listener) {
        ArrayList<Message> data = new ArrayList<>();

        String text[] = {
                "15 September",
                "Hi, Julia! How are you?", "Hi, Joe, looks great! :) ", "I'm fine. Wanna go out somewhere?",
                "Yes! Coffe maybe?", "Great idea! You can come 9:00 pm? :)))", "Ok!",
                "Ow my good, this Kit is totally awesome", "Can you provide other kit?", "I don't have much time, :`(",

                "16 September",
                "https://instagram.com/p/BaaTyGhhzde/media/?size=m",
                "https://i.ytimg.com/vi/MZJSTYXYctc/maxresdefault.jpg",
                "https://znews-photo-td.zadn.vn/w1024/Uploaded/qfssu/2018_02_25/640_1.jpeg",
                "https://znews-photo-td.zadn.vn/w1024/Uploaded/bpivpjbp/2018_08_10/1516915201_qx1280w.jpg",
                "",
                "http://img.docbao.vn/images/fullsize/2018/01/30/sao/duong-mich-bi-chi-trich.jpg"
        };

        String time[] = {
                "", "5:30pm", "5:35pm", "5:36pm", "5:40pm", "5:41pm", "5:42pm", "5:40pm", "5:41pm", "5:42pm",
                "", "5:43pm", "5:44pm", "5:45pm", "5:46pm", "5:47pm", "5:48pm"};

        String type[] = {
                "0", "2", "1", "1", "2", "1", "2", "2", "2", "1",
                "0", "1", "1", "2", "1", "2", "2"};

        for (int i = 0; i < text.length; i++) {
            Message item = new Message();
            item.setType(type[i]);
            item.setTime(time[i]);
            if (i < 10) {
                item.setContentType(Constants.CHAT_CONTENT_TYPE_TEXT);
                item.setText(text[i]);
            } else {
                item.setContentType(Constants.CHAT_CONTENT_TYPE_URI);
                item.setImgUrl(text[i]);
            }
            data.add(item);
        };

        if (listener != null) listener.onSuccess(data);
    }

}
