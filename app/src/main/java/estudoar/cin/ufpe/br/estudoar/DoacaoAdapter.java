package estudoar.cin.ufpe.br.estudoar;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by joaoveras on 06/06/2015.
 */
public class DoacaoAdapter extends ArrayAdapter {
    protected Context mContext;
    protected List mDoacoes;

    public DoacaoAdapter(Context context, List doacoes) {
        super(context, R.layout.materiais_custom, doacoes);
        mContext = context;
        mDoacoes = doacoes;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.materiais_custom, null);
            holder = new ViewHolder();
            holder.nomeDoacao = (TextView) convertView.findViewById(R.id.materialName);
            holder.descricaoDoacao = (TextView) convertView.findViewById(R.id.materialDesc);
            holder.imagemDoacao = (ImageView) convertView.findViewById(R.id.materialImage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

        }

        ParseObject doacaoObject = (ParseObject) mDoacoes.get(position);

        // pega o nome do material
        String username = doacaoObject.getString("nome");
        holder.nomeDoacao.setText(username);

        // pega a descricao do material
        String status = doacaoObject.getString("descricao");
        holder.descricaoDoacao.setText(status);

        // pega a imagem do material
        ParseFile foto = (ParseFile) doacaoObject.get("foto");
        Log.d("foto", foto.getName() + " " + foto.getUrl());
        Picasso.with(mContext)
                .load(foto.getUrl())
                .resize(200, 200).centerCrop()
                .into(holder.imagemDoacao);

        return convertView;
    }

    public static class ViewHolder {
        TextView nomeDoacao;
        TextView descricaoDoacao;
        ImageView imagemDoacao;
    }

}
