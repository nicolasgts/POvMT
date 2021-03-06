package com.projectles.povmt.activitys;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response.Listener;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.projectles.povmt.R;
import com.projectles.povmt.adapters.AtividadesAdapter;
import com.projectles.povmt.api.shared.RequestManager;
import com.projectles.povmt.api.RestClient;
import com.projectles.povmt.models.Atividade;
import com.projectles.povmt.models.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListarAtividadesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private AtividadesAdapter adapter;

    private RestClient client;
    private List<Atividade> atividades = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = new RestClient(this);
        setContentView(R.layout.activity_listatividades_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.list_atividades);

        // Use this setting to improve performance if you know that changes in content do not change
        // the layout size of the RecyclerView.
        recyclerView.setHasFixedSize(true);

        // Use a linear layout manager
        LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Adding adapter to recyclerView
        adapter = new AtividadesAdapter(atividades, this);
        recyclerView.setAdapter(adapter);

        if (Util.isConnectedToInternet(this)) {
            // Get the activities list from server
            client.atividades.getAtividades(new Listener<Atividade[]>() {
                @Override
                public void onResponse(Atividade[] response) {
                    atividades.addAll(Arrays.asList(response));
                    Collections.sort(atividades, Collections.<Atividade>reverseOrder());

                    // Adding adapter to recyclerView
                    adapter = new AtividadesAdapter(atividades, client.getContext());
                    recyclerView.setAdapter(adapter);
                }
            }, new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("RestError", "Fail cause by: " + error.getCause());
                }
            });
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "Sem conexão com a internet, impossível completar a operação",
                    Toast.LENGTH_LONG
            ).show();
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btn_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialogNewAtividade();
            }
        });
    }

    public void createDialogNewAtividade() {
        Builder builder = new AlertDialog.Builder(this);

        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View dialogView = inflater.inflate(R.layout.adicionar_atividade_dialog, null);
        builder.setView(dialogView);

        final EditText edtNome = (EditText) dialogView.findViewById(R.id.edtxt_nome);
        final EditText edtDescricao = (EditText) dialogView.findViewById(R.id.edtxt_descricao);

        // Add action buttons
        builder.setPositiveButton("Criar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                String nome = edtNome.getText().toString();
                String descricao = edtDescricao.getText().toString();

                if(nome.trim().isEmpty() || descricao.trim().isEmpty()) {
                    Toast.makeText(
                            getApplicationContext(),
                            "Algum dos campos esta vazio!", Toast.LENGTH_LONG).show();
                }else if (!Util.isConnectedToInternet(getApplicationContext())) {
                    Toast.makeText(
                            getApplicationContext(),
                            "Sem conexão com a internet, impossível completar a operação",
                            Toast.LENGTH_LONG
                    ).show();
                } else {
                    Map<String, String> params = new HashMap<>();
                    // TODO: Fix this when the user module is created
                    params.put("usuFk", "1");
                    params.put("name", nome);
                    params.put("description", descricao);

                    client.atividades.addAtividade(new Listener<Atividade>() {
                        @Override
                        public void onResponse(Atividade response) {
                            atividades.add(response);
                            Collections.sort(atividades, Collections.<Atividade>reverseOrder());

                            // Adding adapter to recyclerView
                            adapter = new AtividadesAdapter(atividades, client.getContext());
                            recyclerView.setAdapter(adapter);
                        }
                    }, params);
                }
            }
        })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (Util.isConnectedToInternet(this)) {
            client.atividades.getAtividades(new Listener<Atividade[]>() {
                @Override
                public void onResponse(Atividade[] response) {
                    atividades.clear();
                    atividades.addAll(Arrays.asList(response));
                    Collections.sort(atividades, Collections.<Atividade>reverseOrder());

                    // Adding adapter to recyclerView
                    adapter = new AtividadesAdapter(atividades, client.getContext());
                    recyclerView.setAdapter(adapter);
                }
            }, new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("RestError", "FAIL:, cause by: " + error.getMessage());

                    // Adding adapter to recyclerView
                    adapter = new AtividadesAdapter(atividades, client.getContext());
                    recyclerView.setAdapter(adapter);
                }
            });
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "Sem conexão com a internet, impossível completar a operação",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_listatividades_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will.
        // Automatically handle clicks on the Home/Up button, so long as you specify a parent
        // activity in AndroidManifest.
        int id = item.getItemId();

        // Noinspection SimplifiableIfStatement
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_relatorio_semanal) {
            // Handle the camera action
            Intent intent = new Intent(this, RelatorioSemanalActivity.class);
            startActivity(intent);
        }

        else if (id == R.id.nav_relatorio_historico){
            Intent intent = new Intent(this, RelatorioActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStop() {
        super.onStop();
        RequestManager.getInstance(this).stopRequestsInRequestQueueByTag(
                getString(R.string.activities_requests)
        );
    }
}
