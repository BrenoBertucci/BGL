package com.example.bgl;

import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Utilidades de interface (edge-to-edge).
 * Soma os insets das barras do sistema ao padding original da view raiz,
 * para o conteúdo não ficar embaixo da status bar / barra de navegação.
 */
final class Ui {

    private Ui() { }

    static void aplicarInsets(View root) {
        final int pl = root.getPaddingLeft();
        final int pt = root.getPaddingTop();
        final int pr = root.getPaddingRight();
        final int pb = root.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(pl + bars.left, pt + bars.top, pr + bars.right, pb + bars.bottom);
            return insets;
        });
    }
}
