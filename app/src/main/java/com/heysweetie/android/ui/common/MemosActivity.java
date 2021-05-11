package com.heysweetie.android.ui.common;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.heysweetie.android.R;
import com.heysweetie.android.logic.model.Msg;
import com.heysweetie.android.logic.model.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class MemosActivity extends BaseActivity {
    private String userPhone;
    private User user;

    private MaterialToolbar toolBar;
    private RecyclerView memos_recycler;
    private EditText memos_edit;
    private Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memos);
        userPhone = getIntent().getStringExtra("user_phone");
        user = (User) getIntent().getSerializableExtra("user_data");

        initControlUnit();
        initView();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobQuery<Msg> query = new BmobQuery<>();
                query.addWhereEqualTo("userPhone", userPhone).order("-createdAt").findObjects(new FindListener<Msg>() {
                    @Override
                    public void done(List<Msg> msgs, BmobException e) {
                        Msg msg;
                        List<String> msgContent;
                        List<Integer> msgType;
                        if (msgs.size() > 0) {
                            msg = msgs.get(0);
                            msgContent = msg.getMsgContent();
                            msgType = msg.getMsgType();
                            if (user.getAdmin() == 0) {
                                msg.setUserImage(user.getUserImageId());
                                msg.setUserPhone(user.getUsername());
                                msg.setMsgDate(new Date());
                                msgContent.add(memos_edit.getText().toString());
                                msgType.add(1);
                            } else {
                                msgContent.add(memos_edit.getText().toString());
                                msgType.add(0);
                            }
                            msg.setMsContent(msgContent);
                            msg.setMsgType(msgType);
                            msg.update(msg.getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        Toast.makeText(MemosActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                                        memos_edit.setText("");
                                    } else {
                                        Toast.makeText(MemosActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                                    }
                                    refresh();
                                }
                            });
                        } else {
                            msg = new Msg();
                            msgContent = new ArrayList<>();
                            msgType = new ArrayList<>();
                            if (user.getAdmin() == 0) {
                                msg.setUserImage(user.getUserImageId());
                                msg.setUserPhone(user.getUsername());
                                msgContent.add(memos_edit.getText().toString());
                                msg.setMsgDate(new Date());
                                msgType.add(1);
                            } else {
                                msgContent.add(memos_edit.getText().toString());
                                msgType.add(0);
                            }
                            msg.setMsContent(msgContent);
                            msg.setMsgType(msgType);
                            msg.save(new SaveListener<String>() {
                                @Override
                                public void done(String s, BmobException e) {
                                    if (e == null) {
                                        Toast.makeText(MemosActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                                        memos_edit.setText("");
                                    } else {
                                        Toast.makeText(MemosActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                                        memos_edit.setText("");
                                    }
                                    refresh();
                                }
                            });
                        }
                        memos_recycler.scrollToPosition(msg.getMsgContent().size() - 1);//定位到行尾
                    }
                });
            }
        });
    }

    private void initView() {
        //设置标题栏
        toolBar.setTitle("查看留言");
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //半透明状态栏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        memos_recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        refresh();
    }

    private void refresh() {
        BmobQuery<Msg> query = new BmobQuery<>();
        query.addWhereEqualTo("userPhone", userPhone).order("-createdAt").findObjects(new FindListener<Msg>() {
            @Override
            public void done(List<Msg> msgs, BmobException e) {
                if (e == null) {
                    if (msgs.size() > 0) {
                        Msg msg = msgs.get(0);
                        memos_recycler.setAdapter(new MemosAdapter(MemosActivity.this, msg));
                        memos_recycler.scrollToPosition(msg.getMsgContent().size() - 1);
                    }
                } else {
                    Toast.makeText(MemosActivity.this, "没有这个表", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initControlUnit() {
        toolBar = findViewById(R.id.toolBar);
        memos_recycler = findViewById(R.id.memos_recycler);
        memos_edit = findViewById(R.id.memos_edit);
        send = findViewById(R.id.send);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}