package com.infoasso.api.specifications;

import com.infoasso.api.model.Association;
import com.infoasso.api.model.Category;
import com.infoasso.api.model.Schedule;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

public class AssociationSpecifications {

    public static Specification<Association> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                (name == null || name.isBlank()) ? null :
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("displayName")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Association> hasCity(String city) {
        return (root, query, criteriaBuilder) ->
                (city == null || city.isBlank()) ? null :
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("city")), city.toLowerCase() + "%");
    }

    public static Specification<Association> hasCategory(String categoryType) {
        return (root, query, criteriaBuilder) -> {
            if (categoryType == null || categoryType.isBlank()) {
                return null; // Si aucun filtre de catégorie n'est fourni, on ignore
            }

            // On accède directement à la relation 'category' puis au champ 'type' (ou 'label')
            return criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("category").get("type")),
                    categoryType.toLowerCase()
            );
        };
    }

    public static Specification<Association> hasAge(Integer age) {
        return (root, query, criteriaBuilder) -> {
            if (age == null) {
                return null; // Si l'âge est vide, on ignore le filtre
            }

            // On crée une sous-requête pour cibler la table des Schedule
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Schedule> scheduleRoot = subquery.from(Schedule.class);

            // On sélectionne les ID des associations liées aux schedules qui correspondent à l'âge
            subquery.select(scheduleRoot.get("association").get("id"))
                    .where(
                            criteriaBuilder.and(
                                    // Premier bloc : (ageMin <= age OR ageMin IS NULL)
                                    criteriaBuilder.or(
                                            criteriaBuilder.lessThanOrEqualTo(scheduleRoot.get("ageMin"), age),
                                            criteriaBuilder.isNull(scheduleRoot.get("ageMin"))
                                    ),
                                    // Deuxième bloc : (ageMax >= age OR ageMax IS NULL)
                                    criteriaBuilder.or(
                                            criteriaBuilder.greaterThanOrEqualTo(scheduleRoot.get("ageMax"), age),
                                            criteriaBuilder.isNull(scheduleRoot.get("ageMax"))
                                    )
                            )
                    );
            // On retourne la condition : l'id de l'association doit être dans la sous-requête
            return root.get("id").in(subquery);
        };
    }
}