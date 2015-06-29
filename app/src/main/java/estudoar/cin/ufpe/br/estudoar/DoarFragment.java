package estudoar.cin.ufpe.br.estudoar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class DoarFragment extends Fragment implements View.OnClickListener {

    public static final int GET_FROM_GALLERY = 3;

    private EditText nomeImput;
    private EditText assuntoImput;
    private EditText descricaoImput;

    private Button fotoGaleiraBtn;
    private Button fotoCameraBtn;
    private Button doarBtn;
    private Button addEndBtn;

    private LinearLayout enderecoCheck;

    private ParseGeoPoint localizacao;

    private final int GET_PICTURE_CODE = 1;
    private final int GET_LOCATION_CODE = 2;

    private Spinner categorias_spinner;
    private String categoriaSelecionada = "Livro";

    private ImageView fotoView;

    ParseObject doacao;
    ParseUser currentUser;

    public DoarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View doarView = inflater.inflate(R.layout.fragment_doar, container, false);

        currentUser = ParseUser.getCurrentUser();

        if (savedInstanceState == null) {
        }

        nomeImput = (EditText) doarView.findViewById(R.id.nome_doar);
        assuntoImput = (EditText) doarView.findViewById(R.id.assunto_doar);
        descricaoImput = (EditText) doarView.findViewById(R.id.descricao_doar);

        fotoGaleiraBtn = (Button) doarView.findViewById(R.id.btnFotoGaleria);
        fotoCameraBtn = (Button) doarView.findViewById(R.id.btnFotoCamera);
        doarBtn = (Button) doarView.findViewById(R.id.btnDoar);
        addEndBtn = (Button) doarView.findViewById(R.id.btnAddAddress);

        enderecoCheck = (LinearLayout) doarView.findViewById(R.id.enderecoCheck);

        fotoGaleiraBtn.setOnClickListener(this);
        fotoCameraBtn.setOnClickListener(this);
        doarBtn.setOnClickListener(this);
        addEndBtn.setOnClickListener(this);

        fotoView = (ImageView) doarView.findViewById(R.id.fotoView);

        categorias_spinner = (Spinner) doarView.findViewById(R.id.spinner_doar);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.categorias_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categorias_spinner.setAdapter(adapter);

        categorias_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView parentView, View selectedItemView, int position, long id) {
                categoriaSelecionada = (String) categorias_spinner.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView parentView) {

            }
        });

        return doarView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onClick(View v) {
        int key = v.getId();

        switch (key) {
            case R.id.btnDoar:
                doarMaterial(v);
                break;
            case R.id.btnFotoGaleria:
                uploadFotoGaleria(v);
                break;
            case R.id.btnFotoCamera:
                uploadFotoCamera(v);
                break;
            case R.id.btnAddAddress:
                openMaps(v);
                break;
        }
    }

    public void doarMaterial(View view) {

        String nome = nomeImput.getText().toString().trim();

        if (nome.equals("")) {
            Toast.makeText(getActivity(), "Campo 'Nome' Obrigatório!", Toast.LENGTH_SHORT).show();

        }else {
            final Dialog progressDialog = ProgressDialog.show(getActivity(), "", "Salvando...", true);

            String assunto = assuntoImput.getText().toString().trim();
            String descricao = descricaoImput.getText().toString().trim();

            doacao = new ParseObject("Doacao");

            doacao.put("nome", nome);
            doacao.put("categoria", categoriaSelecionada);
            doacao.put("assunto", assunto);
            doacao.put("descricao", descricao);

            if (fotoView.getDrawable() == null) {
                fotoView.setImageResource(R.drawable.doacao_icon);
            }

            Bitmap foto = ((BitmapDrawable) fotoView.getDrawable()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // Compress image to lower quality scale 1 - 100
            foto.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            byte[] image = stream.toByteArray();

            // Create the ParseFile
            String fileName = nome + ".jpg";
            ParseFile file = new ParseFile(fileName.replaceAll("\\s+",""), image);
            // Upload the image into Parse Cloud
            file.saveInBackground();

            doacao.put("foto", file);

            doacao.put("doador", currentUser.getObjectId());

            if(localizacao != null){
                doacao.put("localizacao", localizacao);
            }

            doacao.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    progressDialog.dismiss();
                    if (e == null) {
                        Toast.makeText(getActivity(), "Doacao cadastrada", Toast.LENGTH_SHORT).show();
                        goToVerDoacao();
                        getActivity().finish();
                    } else {
                        Log.d("MyApp Error", e.getMessage());
                        Toast.makeText(getActivity(), "Erro ao cadastrar", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void uploadFotoGaleria(View view) {
        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);

    }

    public void uploadFotoCamera(View view) {
        startActivityForResult(new Intent("android.media.action.IMAGE_CAPTURE"), 1);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request codes
        if( resultCode == Activity.RESULT_OK){
            if ((requestCode == GET_FROM_GALLERY)) {
                Uri selectedImage = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                    fotoView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if (requestCode == 1) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                fotoView.setImageBitmap(bitmap);
            }else if (requestCode == GET_LOCATION_CODE){
                //double[] result = data.getDoubleArrayExtra("position");
                ArrayList<Double> coordenadas = (ArrayList<Double>) data.getSerializableExtra("coordenadas");
                localizacao = new ParseGeoPoint(coordenadas.get(0), coordenadas.get(1));
                enderecoCheck.setVisibility(View.VISIBLE);
            }
        }

        fotoView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Dialog nagDialog = new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                nagDialog.setCancelable(false);
                nagDialog.setContentView(R.layout.preview_image);
                Button btnClose = (Button)nagDialog.findViewById(R.id.btnIvClose);
                ImageView ivPreview = (ImageView)nagDialog.findViewById(R.id.iv_preview_image);
                ivPreview.setImageDrawable(fotoView.getDrawable());
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


    }

    public void openMaps(View v) {
        final LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {
            Intent i = new Intent(getActivity(), AddEnderecoActivity.class);
            startActivityForResult(i, GET_LOCATION_CODE);
        }
    }


    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Seu GPS está desligado, você deseja ativar?")
                .setCancelable(false)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void goToVerDoacao(){
        Intent i = new Intent(getActivity(), VerDoacaoActivity.class);
        i.putExtra("id_doacao", doacao.getObjectId());
        i.putExtra("id_doador", currentUser.getObjectId());
        startActivity(i);
    }
}
