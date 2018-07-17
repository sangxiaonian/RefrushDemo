package sang.com.refrushdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sang.com.easyrefrush.inter.OnRefreshListener;
import sang.com.easyrefrush.refrush.BaseRefrushLayout;


public class MainActivity extends AppCompatActivity {

    private BaseRefrushLayout refrushLayoutView;
    private RecyclerView recyclerView;

    private List<String> datas;
    private MyAdapter myAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        intData();


    }

    private void intData() {
        datas = new ArrayList<>();
        for (int i = 0; i < 13; i++) {
            datas.add("测试数据" + i);
        }
        myAdapter = new MyAdapter(datas);
        recyclerView.setAdapter(myAdapter);



    }

    private void initView() {
        recyclerView = findViewById(R.id.rv);
        refrushLayoutView = findViewById(R.id.xrefresh);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);

        refrushLayoutView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refrushLayoutView.finishRefrush();
                    }
                }, 1500);
            }
        });


    }


    private class MyAdapter extends RecyclerView.Adapter {


        private List<String> datas;

        public MyAdapter(List<String> datas1) {
            this.datas = datas1;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.item, parent, false));
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof MHolder) {
                ((MHolder) holder).initView(datas.get(position), position);
            }
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }
    }

    private class MHolder extends RecyclerView.ViewHolder {

        private TextView tv;

        public MHolder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv);
        }

        public void initView(String data, int position) {
            tv.setText(data);
        }
    }

}
