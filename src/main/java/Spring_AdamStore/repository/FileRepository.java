package Spring_AdamStore.repository;

import Spring_AdamStore.constants.FileType;
import Spring_AdamStore.entity.FileEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {

     Page<FileEntity> findAllByFileType(Pageable pageable, FileType fileType);

}
