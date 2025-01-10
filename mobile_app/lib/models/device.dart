class Device {
  final String id;
  final String name;
  final String associatedIP;
  final String associatedMAC;
  final int heightResolution;
  final int widthResolution;
  final bool recordingMode;
  final bool recordingVideo;

  Device({
    required this.id,
    required this.name,
    required this.associatedIP,
    required this.associatedMAC,
    required this.heightResolution,
    required this.widthResolution,
    required this.recordingMode,
    required this.recordingVideo,
  });

  Map<String, dynamic> toJson() {
    return {
      "ID": id,
      'Name': name,
      'AssociatedIP': associatedIP,
      'AssociatedMAC': associatedMAC,
      'heightResolution': heightResolution,
      'widthResolution': widthResolution,
      'recordingMode': recordingMode,
      'recordingVideo': recordingVideo,
    };
  }
}
