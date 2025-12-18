package com.akademi.egitimtakip.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * LogAction Annotation
 * 
 * Kullanıcı aksiyonlarını otomatik olarak loglamak için kullanılır.
 * Servis metodlarına eklenerek activity_logs tablosuna kayıt atar.
 * 
 * Kullanım:
 * @LogAction(action = "CREATE", entityType = "Egitim", description = "Yeni eğitim oluşturuldu")
 * public EgitimResponseDTO createEgitim(EgitimRequestDTO dto) { ... }
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogAction {
    
    /**
     * Aksiyon türü
     * Örnek: CREATE, UPDATE, DELETE, VIEW, EXPORT, APPROVE, REJECT
     */
    String action();
    
    /**
     * Entity türü
     * Örnek: Egitim, Sorumlu, Proje, Paydas, Egitmen, Faaliyet, Odeme
     */
    String entityType();
    
    /**
     * Açıklama (SpEL expression destekler)
     * Örnek: "Yeni eğitim oluşturuldu: #{result.ad}"
     * Örnek: "Eğitim güncellendi: ID #{args[0]}"
     * Örnek: "Eğitim silindi"
     */
    String description() default "";
    
    /**
     * Entity ID'yi hangi parametreden alacak (0-indexed)
     * -1 ise return value'dan ID alınır (result.getId())
     * 0 ise ilk parametreden (args[0] veya args[0].getId())
     * 1 ise ikinci parametreden (args[1])
     */
    int entityIdParam() default -1;
    
    /**
     * Hata durumunda da loglansın mı?
     */
    boolean logOnError() default false;
}





