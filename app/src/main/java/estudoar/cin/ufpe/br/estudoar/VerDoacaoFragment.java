package estudoar.cin.ufpe.br.estudoar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class VerDoacaoFragment extends Fragment implements View.OnClickListener{

    private TextView nome;
    private TextView categoria;
    private TextView assunto;
    private TextView descricao;

    private TextView label_status;
    private TextView status;

    private ImageView foto;
    private ImageView icon_status;

    private Button queroBtn;
    private Button contatoBtn;
    private Button deletarBtn;

    private ParseUser currentUser;
    private ParseObject doacaoAtual;

    private String id_doador;

    private boolean minhaDoacao = false;

    private int mId = 1;

    public VerDoacaoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null){
        }

        View doacaoView = inflater.inflate(R.layout.fragment_ver_doacao,container,false);

        currentUser = ParseUser.getCurrentUser();

        nome = (TextView) doacaoView.findViewById(R.id.nome_doacao);
        categoria = (TextView) doacaoView.findViewById(R.id.categoria_doacao);
        assunto = (TextView) doacaoView.findViewById(R.id.assunto_doacao);
        descricao = (TextView) doacaoView.findViewById(R.id.descricao_doacao);

        label_status  = (TextView) doacaoView.findViewById(R.id.label_status);
        status = (TextView) doacaoView.findViewById(R.id.status_doacao);
        icon_status = (ImageView) doacaoView.findViewById(R.id.iconStatus);

        foto = (ImageView) doacaoView.findViewById(R.id.foto_doacao);

        queroBtn = (Button) doacaoView.findViewById(R.id.btnQuero);
        contatoBtn = (Button) doacaoView.findViewById(R.id.btnContatoDoador);
        deletarBtn = (Button) doacaoView.findViewById(R.id.btnDeletarDoacao);

        Intent intent = getActivity().getIntent();

        id_doador = intent.getExtras().getString("id_doador");

        if (id_doador.equals(currentUser.getObjectId())){
            contatoBtn.setText("Editar Doação");
            queroBtn.setVisibility(View.GONE);

            label_status.setVisibility(View.GONE);
            status.setVisibility(View.GONE);
            icon_status.setVisibility(View.GONE);

            minhaDoacao = true;
        }else{
            contatoBtn.setVisibility(View.GONE);
            deletarBtn.setVisibility(View.GONE);
        }

        String id_doacao = intent.getExtras().getString("id_doacao");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Doacao");
        query.getInBackground(id_doacao, new GetCallback<ParseObject>() {
            public void done(ParseObject doacao, ParseException e) {
                if (e == null) {
                    doacaoAtual = doacao;
                    nome.setText(doacaoAtual.getString("nome"));
                    categoria.setText(doacaoAtual.getString("categoria"));
                    assunto.setText(doacaoAtual.getString("assunto"));
                    descricao.setText(doacaoAtual.getString("descricao"));

                    final ParseFile image_file = (ParseFile) doacaoAtual.getParseFile("foto");

                    new Thread(new Runnable() {
                        public void run() {
                            foto.post(new Runnable() {
                                public void run() {
                                    if (image_file != null) {
                                        image_file.getDataInBackground(new GetDataCallback() {
                                            @Override
                                            public void done(byte[] data, ParseException e) {
                                                if (e == null) {
                                                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0,data.length);

                                                    if (bmp != null) foto.setImageBitmap(bmp);
                                                }
                                            }
                                        });
                                    } else {
                                        foto.setPadding(10, 10, 10, 10);
                                    }
                                }
                            });
                        }
                    }).start();

                    if(!minhaDoacao) {

                        ParseQuery<ParseObject> queryFavorito = ParseQuery.getQuery("Favoritos");
                        queryFavorito.whereEqualTo("interessado", currentUser.getObjectId());
                        queryFavorito.whereEqualTo("doacao", doacaoAtual.getObjectId());
                        queryFavorito.getFirstInBackground(new GetCallback<ParseObject>() {
                            public void done(ParseObject favorito, ParseException e) {
                                if (favorito != null) {
                                    queroBtn.setText("Não Quero");

                                    label_status.setVisibility(View.VISIBLE);
                                    String situacao = favorito.getString("status");
                                    switch (situacao){
                                        case "E":
                                            status.setText("Em Espera");
                                            icon_status.setImageResource(R.drawable.ic_waitng);
                                            break;
                                        case "S":
                                            status.setText("Aceito");
                                            icon_status.setImageResource(R.drawable.ic_accepted);
                                            contatoBtn.setVisibility(View.VISIBLE);
                                            break;
                                        case "N":
                                            status.setText("Recusado");
                                            icon_status.setImageResource(R.drawable.ic_refused);
                                            break;
                                    }
                                    status.setVisibility(View.VISIBLE);

                                } else {
                                    label_status.setVisibility(View.GONE);
                                    status.setVisibility(View.GONE);
                                    icon_status.setVisibility(View.GONE);
                                }
                            }
                        });
                    }

                } else {
                    Toast.makeText(getActivity(), "ID Desconhecido!", Toast.LENGTH_LONG).show();
                }
            }
        });

        foto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Dialog nagDialog = new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                nagDialog.setCancelable(false);
                nagDialog.setContentView(R.layout.preview_image);
                Button btnClose = (Button)nagDialog.findViewById(R.id.btnIvClose);
                ImageView ivPreview = (ImageView)nagDialog.findViewById(R.id.iv_preview_image);
                ivPreview.setImageDrawable(foto.getDrawable());
                ivPreview.setBackgroundColor(Color.BLACK);

                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        nagDialog.dismiss();
                    }
                });
                nagDialog.show();
            }
        });

        queroBtn.setOnClickListener(this);
        contatoBtn.setOnClickListener(this);
        deletarBtn.setOnClickListener(this);

        return doacaoView;
    }

    @Override
    public void onClick(View v) {
        int key = v.getId();

        switch (key) {
            case R.id.btnQuero:
                marcarInteresse(v);
                break;
            case R.id.btnContatoDoador:
                if(minhaDoacao)
                    goToEditarDoacao(v);
                else
                    goToContatoDoador();
                break;
            case R.id.btnDeletarDoacao:
                deletarDoacao();
                break;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    public void marcarInteresse(View view){
        ParseQuery<ParseObject> queryFavorito = ParseQuery.getQuery("Favoritos");

        queryFavorito.whereEqualTo("interessado", currentUser.getObjectId());
        queryFavorito.whereEqualTo("doacao", doacaoAtual.getObjectId());

        queryFavorito.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject favorito, ParseException e) {
                if (favorito != null){
                    favorito.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null){
                                Toast.makeText(getActivity(), "Favorito deletado com sucesso", Toast.LENGTH_SHORT).show();
                                label_status.setVisibility(View.GONE);
                                status.setVisibility(View.GONE);
                                icon_status.setVisibility(View.GONE);

                            }else{
                                Toast.makeText(getActivity(), "Erro ao deletar o favorito", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    queroBtn.setText("Quero");

                } else {
                    ParseObject fvt = new ParseObject("Favoritos");

                    fvt.put("interessado", currentUser.getObjectId());
                    fvt.put("doacao", doacaoAtual.getObjectId());
                    fvt.put("status", "E");

                    fvt.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                queroBtn.setText("Não Quero");

                                label_status.setVisibility(View.VISIBLE);
                                status.setText("Em Espera");
                                status.setVisibility(View.VISIBLE);

                                icon_status.setImageResource(R.drawable.ic_waitng);
                                icon_status.setVisibility(View.VISIBLE);

                                Toast.makeText(getActivity(), "Doação Salva em Favoritos", Toast.LENGTH_SHORT).show();

                                ParseQuery queryNotify = ParseInstallation.getQuery();
                                //queryNotify.whereEqualTo("objectId",currentUser.getObjectId()); //id_doador
                                JSONObject data = null;

                                try {
                                    data = new JSONObject();
                                    data.put("doador_id", id_doador);
                                    data.put("interessado_id", currentUser.getObjectId());
                                    data.put("interessado_name",currentUser.getUsername());
                                    data.put("action", 1);
                                } catch(JSONException ej) {
                                    ej.printStackTrace();
                                }

                                ParsePush androidPush = new ParsePush();
                                androidPush.setData(data);
                                androidPush.setQuery(queryNotify);
                                androidPush.sendInBackground();

                            } else {
                                Toast.makeText(getActivity(), "Erro ao favoritar doação", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }

    public void goToContatoDoador(){
        Intent i = new Intent(getActivity(), MeuPerfil.class);
        i.putExtra("id_usuario",id_doador);
        startActivity(i);
        /*FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        transaction.replace(R.id.fragment_ver_doacao, new ContatoDoadorFragment());
        transaction.addToBackStack(null);
        transaction.commit();*/
    }

    public void goToEditarDoacao(View view){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        transaction.replace(R.id.fragment_ver_doacao, new EditarDoacaoFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void deletarDoacao(){
        new AlertDialog.Builder(getActivity())
                //.setTitle("Opa!")
                .setMessage("Você deseja deletar esta doação?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        doacaoAtual.deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e == null){
                                    Toast.makeText(getActivity(), "Doação Excluída", Toast.LENGTH_SHORT).show();
                                    getActivity().finish();
                                }else{
                                    Toast.makeText(getActivity(), "Erro ao Deletar", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

}
