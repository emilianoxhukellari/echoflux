package transcribe.application.core.jpa.core;

public enum JpaPropertyType {

    /**
     * The property is an identifier. There has to be exactly one ID property per jpa dto.
     * An ID property cannot be an audit property, a version property, or a parent property.
     */
    ID,

    /**
     * The property is an audit property. There can be multiple audit properties per jpa dto. An audit property cannot
     * be a parent property or a version property.
     */
    AUDIT,

    /**
     * The property is a version property. There can be only up to one version property per jpa dto.
     */
    VERSION,

    /**
     * This is a hint that the property contains more properties.
     */
    PARENT,

    /**
     * This is a hint that the property should be hidden from the user.
     */
    HIDDEN,

    /**
     * A core property is a property that is not an audit property, not and ID property, not a version property,
     * not a parent property, and not a hidden property.
     */
    CORE,

}
